package com.evergarden.cms.app.router;

import com.hanami.sdk.router.HttpMethod;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

public class RouteAgregate {
	private HttpMethod                      method;
	private String                          path;
	private HandlerFunction<ServerResponse> handler;
	
	public RouteAgregate(HttpMethod method, String path, HandlerFunction<ServerResponse> handler) {
		this.method = method;
		this.path = path;
		this.handler = handler;
	}
	
	public HttpMethod getMethod() {
		return method;
	}
	
	public String getPath() {
		return path;
	}
	
	public HandlerFunction<ServerResponse> getHandler() {
		return handler;
	}
}
