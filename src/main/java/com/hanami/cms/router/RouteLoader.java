package com.hanami.cms.router;

import com.hanami.sdk.router.HttpMethod;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;

@Configuration
public class RouteLoader {
	
	@Bean
	public RouterFunction loadRoute() throws NoSuchMethodException, NoSuchAlgorithmException, IllegalAccessException, InvocationTargetException {
		RouteBuilder builder = new RouteBuilder();
		
		builder.add(HttpMethod.get, "/tibouloute", (ServerRequest request)->{
			return ServerResponse.ok().body(BodyInserters.fromObject("Hello tibouloute"));
		});
		
		return builder.build();
	}
}
