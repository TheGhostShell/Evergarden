package com.hanami.sdk.router;


import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public interface MonoliteRouteInterface {

    String getPath();

    HttpMethod getMethod();

    Mono<ServerResponse> getHandler(ServerRequest request);
}
