package com.evergarden.cms.context.user.application.config;

import com.evergarden.cms.context.user.application.service.CRUDRoleService;
import com.evergarden.cms.context.user.domain.entity.Role;
import com.evergarden.cms.context.user.domain.entity.RoleEnum;
import com.evergarden.cms.context.user.domain.entity.User;
import com.evergarden.cms.app.config.security.EvergardenEncoder;
import com.evergarden.cms.context.user.infrastructure.persistence.RoleRepository;
import com.evergarden.cms.context.user.infrastructure.persistence.UserRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class DefaultAdminConfig {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private CRUDRoleService cRUDRoleService;
    private Logger logger;
    private EvergardenEncoder encoder;

    @Autowired
    public DefaultAdminConfig(UserRepository userRepository,
                              RoleRepository roleRepository,
                              CRUDRoleService cRUDRoleService,
                              EvergardenEncoder encoder,
                              Logger logger) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.cRUDRoleService = cRUDRoleService;
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

        logger.debug("No user with role MASTER_ADMIN found try to create one");

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
            .roles(roles)
            .encodedCredential(encoder.getEncodedCredential())
            .build();

        return cRUDRoleService.assignRoleToUser(admin)
            .flatMap(user -> userRepository.save(user))
            .doOnError(throwable -> logger.error("We failed bad in creating default user"));
    }
}
