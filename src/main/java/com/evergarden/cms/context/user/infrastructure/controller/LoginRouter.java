package com.evergarden.cms.context.user.infrastructure.controller;

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
		String pathPublic = env.getProperty("v1");
		
		return RouterFunctions.route(RequestPredicates.POST(pathPublic + "/login"), handler::login)
			.andRoute(RequestPredicates.POST(pathPublic + "/guest"), handler::guest)
			.andRoute(RequestPredicates.POST(pathSecure + "/logout"), handler::logout)
			.andRoute(RequestPredicates.GET("/admin"), handler::admin)
			.andRoute(RequestPredicates.GET("/*"), handler::home);
	}
}
