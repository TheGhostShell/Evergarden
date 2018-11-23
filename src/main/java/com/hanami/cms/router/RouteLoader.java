package com.hanami.cms.router;

import com.hanami.sdk.router.HandlerFunction;
import com.hanami.sdk.router.HttpMethod;
import com.hanami.sdk.router.Route;
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
		
		HandlerFunction handler = (com.hanami.sdk.router.ServerRequest request)->{
			return new com.hanami.sdk.router.ServerResponse(request.getPath().getPath());
		};
		
		Route route = new Route("/tibou", HttpMethod.get, handler);
		
		builder.add(route.getMethod(), route.getPath(), HandlerConverter.toHandlerFunction(route))
			.add(HttpMethod.get, "/test", (ServerRequest request)->{
				return ServerResponse.ok().body(BodyInserters.fromObject("ceci est un test"));
			});
		
		builder.add(HttpMethod.get, "/working", (ServerRequest request)->{
			return ServerResponse.ok().body(BodyInserters.fromObject("success magistral"));
		});
		
		return builder.build();
	}
}
