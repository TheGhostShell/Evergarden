package com.evergarden.cms.context.router;

import com.hanami.sdk.router.*;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * Convert hanami.sdk.router.Handler to generic Spring handler
 */
public class HandlerConverter {
	
	public Route route;
	
	public HandlerConverter(Route route) {
		this.route = route;
	}
	
	public Mono<ServerResponse> toServerResponse(ServerRequest request) {
		
		Request sdkRequest = new Request(
			new Header(),
			new Path(request.path()),
			new QueryParameters(),
			new Body("no data")
		);
		
		Response sdkResponse = route.getHandler().apply(sdkRequest);
		
		return ServerResponse.ok().body(BodyInserters.fromObject(sdkResponse));
	}
	
	public org.springframework.web.reactive.function.server.HandlerFunction toHandlerFunction() {
		return (ServerRequest request) -> {
			Request sdkRequest = new Request(
				new Header(),
				new Path(request.path()),
				new QueryParameters(),
				new Body("no data")
			);
			
			Response sdkResponse = route.getHandler().apply(sdkRequest);
			
			return ServerResponse.ok().body(BodyInserters.fromObject(sdkResponse.getBody().getJson()));
		};
	}
	
	public static org.springframework.web.reactive.function.server.HandlerFunction toHandlerFunction(Route route) {
		return (ServerRequest request) -> {
			Request sdkRequest = new Request(
				new Header(),
				new Path(request.path()),
				new QueryParameters(),
				new Body(request.path())
			);
			
			Response sdkResponse = route.getHandler().apply(sdkRequest);
			
			return ServerResponse.ok().body(BodyInserters.fromObject(sdkResponse.getBody().getJson()));
		};
	}
}
