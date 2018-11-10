package com.hanami.sdk.router;

import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public class Route implements MonoliteRouteInterface{

    private String path;
    private HttpMethod method;
    private HandlerFunction handler;
    
    public Route(String path, HttpMethod method, HandlerFunction handler) {
        this.path = path;
        this.method = method;
        this.handler = handler;
    }
    
    public String getPath() {
        return path;
    }
    
    public HttpMethod getMethod() {
        return method;
    }
    
    @Override
    public Mono<ServerResponse> getHandler(ServerRequest request) {
        return null;
    }
    
    public HandlerFunction getHandler() {
        return handler;
    }
}
