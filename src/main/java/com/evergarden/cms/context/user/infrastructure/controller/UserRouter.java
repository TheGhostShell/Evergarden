package com.evergarden.cms.context.user.infrastructure.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class UserRouter {

    @Bean
    public RouterFunction<ServerResponse> userRoute(UserHandler handler, Environment env) {

        String pathSecure = env.getProperty("v1s");

        return RouterFunctions.route(RequestPredicates.POST(pathSecure + "/user"), handler::create)
            .andRoute(RequestPredicates.GET(pathSecure + "/user/{id}"), handler::read)
            .andRoute(RequestPredicates.PUT(pathSecure + "/user"), handler::update)
            .andRoute(RequestPredicates.PUT(pathSecure + "/user/password"), handler::updatePassword)
            .andRoute(RequestPredicates.GET(pathSecure + "/user"), handler::show)
            .andRoute(RequestPredicates.GET(pathSecure + "/user/avatar/{userId}"), handler::readAvatar)
            .andRoute(RequestPredicates.POST(pathSecure + "/user/avatar"), handler::updateAvatar);
    }
}
