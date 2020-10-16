package com.evergarden.cms.context.user.infrastructure.controller;

import com.evergarden.cms.context.user.application.service.CRUDProfileService;
import com.evergarden.cms.context.user.application.service.CRUDRoleService;
import com.evergarden.cms.context.user.domain.entity.Profile;
import com.evergarden.cms.context.user.domain.entity.Role;
import com.evergarden.cms.context.user.infrastructure.controller.input.ProfileRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProfileHandler {

    private CRUDProfileService crudProfileService;
    private CRUDRoleService    crudRoleService;

    public ProfileHandler(CRUDProfileService crudProfileService, CRUDRoleService crudRoleService) {
        this.crudProfileService = crudProfileService;
        this.crudRoleService = crudRoleService;
    }

    public Mono<ServerResponse> create(ServerRequest serverRequest) {
        return serverRequest.body(BodyExtractors.toMono(ProfileRequest.class))
            .map(profileRequest -> {
                List<Role> roles = profileRequest.getRoles()
                    .stream()
                    .map(roleRequest -> new Role(roleRequest.getRole()))
                    .collect(Collectors.toList());

                return Profile.builder()
                    .name(profileRequest.getName())
                    .roles(roles)
                    .build();
            })
            .flatMap(profile -> crudRoleService.saveRoles(profile.getRoles())
                .flatMap(roles -> crudProfileService.save(Profile.builder()
                    .name(profile.getName())
                    .roles(roles)
                    .build())
                ))
            .flatMap(profile -> ServerResponse.ok().bodyValue(profile));
    }

    public Mono<ServerResponse> read(ServerRequest serverRequest) {
        return crudProfileService.findById(serverRequest.pathVariable("id"))
            .flatMap(profile -> ServerResponse.ok().bodyValue(profile))
            .onErrorResume(throwable -> ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        return ServerResponse.ok().body(crudProfileService.showProfiles(), Profile.class);
    }
}
