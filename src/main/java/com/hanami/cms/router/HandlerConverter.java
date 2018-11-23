package com.hanami.cms.router;

import com.hanami.sdk.router.*;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * Convert hanami.sdk.router.HandlerFunction to generic Spring handler
 */
public class HandlerConverter {
	
	public Route route;
	
	public HandlerConverter(Route route) {
		this.route = route;
	}
	
	public Mono<ServerResponse> toServerResponse(ServerRequest request) {
		//com.hanami.sdk.router.HandlerFunction handler = route.getHandler();
		
		com.hanami.sdk.router.ServerRequest sdkRequest = new com.hanami.sdk.router.ServerRequest(new Path(request.path()));
		
		com.hanami.sdk.router.ServerResponse sdkResponse = route.getHandler().apply(sdkRequest);
		
		return ServerResponse.ok().body(BodyInserters.fromObject(sdkResponse.getFake()));
	}
	
	public org.springframework.web.reactive.function.server.HandlerFunction toHandlerFunction() {
		return (ServerRequest request) -> {
			com.hanami.sdk.router.ServerRequest sdkRequest = new com.hanami.sdk.router.ServerRequest(new Path(request.path()));
			
			com.hanami.sdk.router.ServerResponse sdkResponse = route.getHandler().apply(sdkRequest);
			
			return ServerResponse.ok().body(BodyInserters.fromObject(sdkResponse.getFake()));
		};
	}
	
	public static org.springframework.web.reactive.function.server.HandlerFunction toHandlerFunction(Route route) {
		return (ServerRequest request) -> {
			com.hanami.sdk.router.ServerRequest sdkRequest = new com.hanami.sdk.router.ServerRequest(new Path(request.path()));
			
			com.hanami.sdk.router.ServerResponse sdkResponse = route.getHandler().apply(sdkRequest);
			
			return ServerResponse.ok().body(BodyInserters.fromObject(sdkResponse.getFake()));
		};
	}
}
