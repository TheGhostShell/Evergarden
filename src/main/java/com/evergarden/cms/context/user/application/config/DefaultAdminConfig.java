package com.evergarden.cms.context.user.application.config;

import com.evergarden.cms.app.config.security.EvergardenEncoder;
import com.evergarden.cms.context.user.application.service.CRUDProfileService;
import com.evergarden.cms.context.user.application.service.CRUDRoleService;
import com.evergarden.cms.context.user.application.service.CRUDUserService;
import com.evergarden.cms.context.user.domain.entity.Profile;
import com.evergarden.cms.context.user.domain.entity.ProfileEnum;
import com.evergarden.cms.context.user.domain.entity.Role;
import com.evergarden.cms.context.user.domain.entity.RoleEnum;
import com.evergarden.cms.context.user.domain.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Slf4j
public class DefaultAdminConfig {

    private CRUDUserService    crudUserService;
    private CRUDProfileService crudProfileService;
    private CRUDRoleService    crudRoleService;
    private EvergardenEncoder  encoder;

    @Autowired
    public DefaultAdminConfig(CRUDUserService crudUserService, CRUDProfileService crudProfileService,
                              CRUDRoleService crudRoleService, EvergardenEncoder encoder) {
        this.crudUserService = crudUserService;
        this.crudProfileService = crudProfileService;
        this.crudRoleService = crudRoleService;
        this.encoder = encoder;
    }

    @Bean
    public void createDefaultAdmin() {

        crudProfileService.findByNameOrId("ADMINISTRATOR", "")
            .flatMap(this::findOneAdministrator)
            .doOnError(throwable -> {
                log.warn("No user with administrator profile {}", throwable.getMessage());
                createMasterAdmin().subscribe();
            })
            .subscribe();
    }

    private Mono<User> createMasterAdmin() {

        log.debug("No user with profile ADMINISTRATOR found, try to create one");

        List<Role> roles = new ArrayList<>();
        roles.add(Role.createFromRawValue(RoleEnum.MASTER_ADMIN.toString()));
        roles.add(Role.createFromRawValue(RoleEnum.ADMIN.toString()));
        roles.add(Role.createFromRawValue(RoleEnum.GUEST.toString()));

        Profile profile = Profile.builder()
            .roles(roles)
            .name(ProfileEnum.ADMINISTRATOR.name())
            .build();

        encoder.encode("pass");

        return crudRoleService.saveRoles(roles)
            .map(roleList -> Profile.builder().roles(roleList).name(profile.getName()).build())
            .flatMap(profile1 -> crudProfileService.save(profile1))
            .flatMap(profileSaved -> {
                User admin = User.builder()
                    .email("violet@mail.com")
                    .pseudo("violet")
                    .activated(true)
                    .firstname("Violet")
                    .lastname("Evergarden")
                    .profile(profileSaved)
                    .encodedCredential(encoder.getEncodedCredential())
                    .build();

                return crudUserService.updateOrSave(admin);
            })
            .doOnSuccess(user -> log.debug("Successfully created default master user with id: {}", user.getId()))
            .doOnError(throwable -> log.error("We failed bad in creating default user" + throwable.getMessage()));
    }

    private Mono<User> findOneAdministrator(Profile profile) {
        log.debug("Try to find one user with profile ADMINISTRATOR");
        return crudUserService.findFirstByProfile(profile)
            .doOnSuccess(user -> log.debug("found one user with email: {}", user.getEmail()));
    }
}
