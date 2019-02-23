package com.evergarden.sdk.router;

import java.util.function.Function;

public class Route implements MonolithRouteInterface {
	
	private String                      path;
	private HttpMethod                  method;
	private Function<Request, Response> handler;
	
	public Route(String path, HttpMethod method, Function<Request, Response> handler) {
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
	
	public Function<Request, Response> getHandler() {
		return handler;
	}
}
