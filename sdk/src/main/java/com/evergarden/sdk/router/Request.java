package com.evergarden.sdk.router;

public class Request {
	private Header          header;
	private Path            path;
	private QueryParameters query;
	private Body            body;
	
	public Request(Header header, Path path, QueryParameters query, Body body) {
		this.header = header;
		this.path = path;
		this.query = query;
		this.body = body;
	}
	
	public Header getHeader() {
		return header;
	}
	
	public Path getPath() {
		return path;
	}
	
	public QueryParameters getQuery() {
		return query;
	}
	
	public Body getBody() {
		return body;
	}
}
