package com.evergarden.cms.context.user.infrastructure.controller;

import com.evergarden.cms.context.user.application.service.CRUDRoleService;
import com.evergarden.cms.context.user.domain.entity.Role;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class RoleHandler {

    private CRUDRoleService crudRoleService;

    public RoleHandler(CRUDRoleService crudRoleService) {
        this.crudRoleService = crudRoleService;
    }

    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        return ServerResponse.ok().body(crudRoleService.findAllRole(), Role.class);
    }

    public Mono<ServerResponse> read(ServerRequest serverRequest) {
        return crudRoleService.findById(serverRequest.pathVariable("id"))
            .flatMap(role -> ServerResponse.ok().bodyValue(role))
            .onErrorResume(throwable -> ServerResponse.notFound().build());
    }
}
