package com.evergarden.cms.context.user.application.service;

import com.evergarden.cms.app.config.security.JwtHelper;
import com.evergarden.cms.context.user.domain.entity.Guest;
import com.evergarden.cms.context.user.domain.entity.Profile;
import com.evergarden.cms.context.user.domain.entity.Role;
import com.evergarden.cms.context.user.domain.entity.RoleEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class GenerateGuestTokenService {

    private JwtHelper jwtHelper;

    private CRUDProfileService crudProfileService;

    private CRUDRoleService crudRoleService;

    public GenerateGuestTokenService(JwtHelper jwtHelper, CRUDProfileService crudProfileService,
                                     CRUDRoleService crudRoleService) {
        this.jwtHelper = jwtHelper;
        this.crudProfileService = crudProfileService;
        this.crudRoleService = crudRoleService;
    }

    public Mono<Guest> generateGuestToken(Guest guestMono){
        // TODO extract email from request if present return error if no subject field filled
        return Mono.just(guestMono)
            .flatMap(guest -> {
                log.debug("Generating guest token for subject "+ guest.getSubject());
                ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority(RoleEnum.GUEST.name()));
                String subject = guest.getSubject();
                Guest guest1 = new Guest();
                if (guest.getSubject() == null) {
                    subject = "unknown";
                }
                log.debug("Guest subject is " + subject);

                String finalSubject = subject;
                return crudProfileService.findByNameOrId("GUEST", "null")
                    .onErrorResume(throwable -> {
                        List<Role> roles = Collections.singletonList(Role.createFromRawValue("GUEST"));
                        return createProfileAndRole(Profile.builder().name("GUEST").roles(roles).build());
                    })
                    .doOnNext(profile -> log.debug("Founded profile " + profile.getName()))
                    .map(profile -> jwtHelper.generateToken(finalSubject, authorities, 1L, "", profile).getToken())
                    .doOnNext(token -> log.debug("New token generated " + token))
                    .map(token -> {
                        guest1.setToken(token);
                        guest1.setSubject(finalSubject);
                        return guest1;
                    });
            });
    }

    private Mono<Profile> createProfileAndRole(Profile profile) {
        return crudRoleService.saveRoles(profile.getRoles())
            .flatMap(roles -> crudProfileService.save(Profile.builder().name(profile.getName()).roles(roles).build()))
            .doOnError(throwable -> log.error(throwable.getMessage()));
    }
}
