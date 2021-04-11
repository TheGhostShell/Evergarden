package com.evergarden.cms.context.user.infrastructure.controller;

import com.evergarden.cms.context.user.application.service.CRUDProfileService;
import com.evergarden.cms.context.user.application.service.CRUDRoleService;
import com.evergarden.cms.context.user.domain.entity.Profile;
import com.evergarden.cms.context.user.domain.entity.Role;
import com.evergarden.cms.context.user.infrastructure.controller.input.ProfileRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ProfileHandler {

    private final CRUDProfileService crudProfileService;
    private final CRUDRoleService crudRoleService;

    public ProfileHandler(CRUDProfileService crudProfileService, CRUDRoleService crudRoleService) {
        this.crudProfileService = crudProfileService;
        this.crudRoleService = crudRoleService;
    }

    public Mono<ServerResponse> create(ServerRequest serverRequest) {
        return serverRequest
            .body(BodyExtractors.toMono(ProfileRequest.class))
            .map(
                profileRequest -> {
                    log.debug("Mapping profile request to profile");
                    List<Role> roles =
                        profileRequest.getRoles().stream()
                            .map(roleRequest -> new Role(roleRequest.getRole()))
                            .collect(Collectors.toList());
                    // TODO use specific mapper
                    return Profile.builder().name(profileRequest.getName()).roles(roles).build();
                })
            .flatMap(
                profile ->
                    crudRoleService
                        .saveRoles(profile.getRoles())
                        .flatMap(
                            roles -> crudProfileService.save(Profile.builder()
                                .name(profile.getName())
                                .roles(roles)
                                .build()
                            )
                        ))
            .flatMap(profile -> ServerResponse.ok().bodyValue(profile))
            .onErrorResume(throwable -> ServerResponse.badRequest().build());
    }

    public Mono<ServerResponse> read(ServerRequest serverRequest) {
        return crudProfileService
            .findById(serverRequest.pathVariable("id"))
            .flatMap(profile -> ServerResponse.ok().bodyValue(profile))
            .onErrorResume(throwable -> ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        return ServerResponse.ok().body(crudProfileService.showProfiles(), Profile.class);
    }
}
