package com.evergarden.cms.context.user.infrastructure.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RoleRouter {

    @Value("${v1s}")
    private String pathSecure;

    @Bean
    public RouterFunction<ServerResponse> roleRoute(RoleHandler handler) {

        return RouterFunctions.route(RequestPredicates.GET(pathSecure + "/role"), handler::show)
            .andRoute(RequestPredicates.GET(pathSecure + "/role/{id}"), handler::read);
    }
}
