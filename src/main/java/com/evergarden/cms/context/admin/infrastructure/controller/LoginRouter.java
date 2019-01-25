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
		
		String pathSecure = env.getProperty("v1s");
		String pathPublic = env.getProperty("v1s");
		
		return RouterFunctions.route(RequestPredicates.POST(pathPublic + "/login"), handler::login)
			//.andRoute(RequestPredicates.GET("/"), handler::login)
			.andRoute(RequestPredicates.POST(pathPublic + "/guest"), handler::guest)
			.andRoute(RequestPredicates.POST(pathSecure + "/user"), handler::create)
			.andRoute(RequestPredicates.GET(pathSecure + "/user/{id}"), handler::read)
			.andRoute(RequestPredicates.PUT(pathSecure + "/user"), handler::update)
			.andRoute(RequestPredicates.GET(pathSecure + "/user"), handler::show);
	}
}
