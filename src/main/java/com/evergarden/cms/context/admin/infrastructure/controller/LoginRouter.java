package com.evergarden.cms.context.admin.infrastructure.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.server.ResponseStatusException;

@Configuration
public class LoginRouter {
	
	@Bean
	public RouterFunction<ServerResponse> loginRoute(LoginHandler handler, Environment env) {
		
		String pathSecure = env.getProperty("v1s");
		String pathPublic = env.getProperty("v1");
		
		return RouterFunctions.route(RequestPredicates.POST(pathPublic + "/login"), handler::login)
			.andRoute(RequestPredicates.POST(pathPublic + "/guest"), handler::guest)
			.andRoute(RequestPredicates.POST(pathSecure + "/user"), handler::create)
			.andRoute(RequestPredicates.GET(pathSecure + "/user/{id}"), handler::read)
			.andRoute(RequestPredicates.PUT(pathSecure + "/user"), handler::update)
			.andRoute(RequestPredicates.GET(pathSecure + "/user"), handler::show)
			.andRoute(RequestPredicates.GET("/admin"), handler::admin)
			.andRoute(RequestPredicates.GET("/*"), handler::home);
	}
}
