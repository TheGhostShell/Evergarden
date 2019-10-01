package com.evergarden.cms.context.user.infrastructure.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RoleRouter {

    @Bean
    public RouterFunction<ServerResponse> roleRoute(RoleHandler handler, Environment env) {
        String pathSecure = env.getProperty("v1s");

        return RouterFunctions.route(RequestPredicates.GET(pathSecure + "/role"), handler::show);
    }
}
