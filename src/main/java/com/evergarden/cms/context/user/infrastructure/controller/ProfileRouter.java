package com.evergarden.cms.context.user.infrastructure.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ProfileRouter {


    @Bean
    public RouterFunction<ServerResponse> profileRoute(ProfileHandler handler,
                                                       @Value("v1s") String path) {

        return RouterFunctions.route(RequestPredicates.POST(path + "/profile"), handler::create)
            .andRoute(RequestPredicates.GET(path + "/profile/{id}"), handler::read)
            .andRoute(RequestPredicates.GET(path + "/profile"), handler::show);
    }
}
