package com.evergarden.cms.context.publisher.infrastructure.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class PostRouter {
	
	@Bean
	public RouterFunction<ServerResponse> postRoute(PostHandler handler, Environment env) {
		return RouterFunctions
			.route(RequestPredicates.POST(env.getProperty("v1s") + "/post")
				.and(RequestPredicates.contentType(MediaType.APPLICATION_JSON)), handler::create)
			.andRoute(RequestPredicates.GET(env.getProperty("v1") + "/post/{id}"), handler::read)
			.andRoute(RequestPredicates.GET(env.getProperty("v1") + "/post"), handler::show)
			.andRoute(RequestPredicates.PATCH(env.getProperty("v1s") + "/post/{id}")
				.and(RequestPredicates.contentType(MediaType.APPLICATION_JSON)), handler::update)
			.andRoute(RequestPredicates.DELETE(env.getProperty("v1s") + "/post/{id}"), handler::delete);
	}
}
