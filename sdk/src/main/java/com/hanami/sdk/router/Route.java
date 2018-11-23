package com.hanami.sdk.router;

import java.util.function.Function;

public class Route implements MonolithRouteInterface {

	private String                                  path;
	private HttpMethod                              method;
	private Function<ServerRequest, ServerResponse> handler;

	public Route(String path, HttpMethod method, Function<ServerRequest, ServerResponse> handler) {
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

	public Function<ServerRequest, ServerResponse> getHandler() {
		return handler;
	}
}
