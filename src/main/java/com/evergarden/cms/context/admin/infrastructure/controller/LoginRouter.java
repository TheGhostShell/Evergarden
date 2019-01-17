package com.evergarden.cms.context.admin.infrastructure.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class LoginRouter {

    @Bean
    public RouterFunction<ServerResponse> loginRoute(LoginHandler handler, Environment env) {
        return RouterFunctions.route(RequestPredicates.POST("/login"), handler::login)
                .andRoute(RequestPredicates.GET("/"), handler::login)
                .andRoute(RequestPredicates.POST(env.getProperty("v1") + "/guest"), handler::guest)
                .andRoute(RequestPredicates.POST(env.getProperty("v1s") + "/user"), handler::create)
                .andRoute(RequestPredicates.GET(env.getProperty("v1s") + "/user/{id}"), handler::read)
                .andRoute(RequestPredicates.PUT(env.getProperty("v1s") + "/user"), handler::update)
                .andRoute(RequestPredicates.GET(env.getProperty("v1s") + "/user"), handler::show);
    }
}
