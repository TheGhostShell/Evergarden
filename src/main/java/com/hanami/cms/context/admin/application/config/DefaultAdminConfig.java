package com.hanami.cms.context.admin.application.config;

import com.hanami.cms.context.admin.application.jwt.EvergardenEncoder;
import com.hanami.cms.context.admin.domain.entity.Role;
import com.hanami.cms.context.admin.domain.entity.RoleEnum;
import com.hanami.cms.context.admin.domain.entity.User;
import com.hanami.cms.context.admin.domain.entity.UserMappingInterface;
import com.hanami.cms.context.admin.infrastructure.persistence.UserRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class DefaultAdminConfig {

    private UserRepository userRepository;

    private Logger logger;

    private EvergardenEncoder encoder;

    @Autowired
    public DefaultAdminConfig(UserRepository userRepository, Logger logger, EvergardenEncoder encoder) {
        this.userRepository = userRepository;
        this.logger = logger;
        this.encoder = encoder;
    }

    @Bean
    public void createDefaultAdmin() {

        userRepository.findFirstByRole(Role.createFromRawValue(RoleEnum.MASTER_ADMIN.toString()))
                .doOnError(throwable -> {

                    logger.warn("No master admin found auto generate default admin "+throwable.toString());

                    createMasterAdmin()
                            .doOnError(throwable1 -> logger.error("Failed to create master admin "
                                    + throwable1.toString())
                            )
                            .subscribe();
                })
                .subscribe();
    }

    private Mono<UserMappingInterface> createMasterAdmin() {

        encoder.encode("pass");

        User admin = new User();

        return Mono.just(admin)
                .map(user -> {

                    user.setEmail("violet@mail.com");
                    user.setActivated(true);
                    user.addRole(Role.createFromRawValue(RoleEnum.MASTER_ADMIN.toString()));
                    user.setFirstname("Violet");
                    user.setLastname("Evergarden");
                    user.setEncodedCredential(encoder.getEncodedCredentials());

                    userRepository
                            .create(user)
                            .doOnError(throwable -> logger.error("Failed to insert data on user table "
                                    + throwable.toString())
                            )
                            .subscribe(userMappingInterface -> {
                                if (userMappingInterface.getId() > 0) {
                                    logger.info("Successfully created master admin");
                                }
                            });

                    return user;
                });
    }
}
