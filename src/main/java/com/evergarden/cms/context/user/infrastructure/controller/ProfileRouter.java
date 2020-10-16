package com.evergarden.cms.context.user.infrastructure.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ProfileRouter {

    @Value("${v1s}")
    String pathSecure;

    @Bean
    public RouterFunction<ServerResponse> profileRoute(ProfileHandler profileHandler) {

        return RouterFunctions.route(RequestPredicates.POST(pathSecure + "/profile"), profileHandler::create)
            .andRoute(RequestPredicates.GET(pathSecure + "/profile/{id}"), profileHandler::read)
            .andRoute(RequestPredicates.GET(pathSecure + "/profile"), profileHandler::show);
    }
}
