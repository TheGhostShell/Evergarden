package com.hanami.cms.web.controller.publisher;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class PostRouter {
    
    @Bean
    public RouterFunction<ServerResponse> route(PostHandler handler, Environment env) {
        return RouterFunctions.route(RequestPredicates.POST(env.getProperty("v1s") + "/post"), handler::create);
    }
}
