package com.evergarden.cms.context.admin.application.config;

import com.evergarden.cms.context.admin.domain.entity.Role;
import com.evergarden.cms.context.admin.domain.entity.RoleEnum;
import com.evergarden.cms.context.admin.domain.entity.User;
import com.evergarden.cms.context.admin.domain.security.EvergardenEncoder;
import com.evergarden.cms.context.admin.infrastructure.persistence.RoleRepository;
import com.evergarden.cms.context.admin.infrastructure.persistence.UserRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Configuration
public class DefaultAdminConfig {

    private UserRepository userRepository;
    private RoleRepository roleRepository;

    private Logger logger;

    private EvergardenEncoder encoder;

    @Autowired
    public DefaultAdminConfig(
        UserRepository userRepository,
        RoleRepository roleRepository,
        EvergardenEncoder encoder,
        Logger logger
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.logger = logger;
        this.encoder = encoder;
    }

    @Bean
    public void createDefaultAdmin() {

        roleRepository.findByRole(RoleEnum.MASTER_ADMIN.toString())
            .flatMap(role -> userRepository.findFirstByRoles(role))
            .switchIfEmpty(
                Mono.defer(this::createMasterAdmin)
            ).subscribe();
    }

    private Mono<User> createMasterAdmin() {

        logger.info("No user with role MASTER_ADMIN found try to create one");

        List<Role> roles = new ArrayList<>();
        roles.add(Role.createFromRawValue(RoleEnum.MASTER_ADMIN.toString()));
        roles.add(Role.createFromRawValue(RoleEnum.ADMIN.toString()));
        roles.add(Role.createFromRawValue(RoleEnum.GUEST.toString()));

        encoder.encode("pass");

        User admin = User.builder()
            .email("violet@mail.com")
            .password(encoder.getEncodedCredential().getEncodedPassword())
            .salt(encoder.getEncodedCredential().getSalt())
            .pseudo("violet")
            .activated(true)
            .firstname("Violet")
            .lastname("Evergarden")
            .encodedCredential(encoder.getEncodedCredential())
            .build();

        return Flux.fromIterable(roles)
            .flatMap(role -> roleRepository.findByRole(role.getRoleValue())
                .map(admin::addRole)
                .switchIfEmpty(Mono.defer(() -> {
                    logger.warn("No role found in db "+ role.getRoleValue());
                    return roleRepository.save(role)
                        .map(role1 -> {
                            admin.addRole(role1);
                            return role1;
                        })
                        .then(Mono.just(admin));
                    }
                )))
            .then(userRepository.save(admin))
            .doOnError(throwable -> logger.error("Shxt we failed bad in creating default user"));
    }
}
