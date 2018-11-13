package com.hanami.sdk.router;

public interface MonoliteRouteInterface {

    String getPath();

    HttpMethod getMethod();

    Mono<ServerResponse> getHandler(ServerRequest request);
}
