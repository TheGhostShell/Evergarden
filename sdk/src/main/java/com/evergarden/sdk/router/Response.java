package com.evergarden.sdk.router;

public class Response {
	private Header     header;
	private Body       body;
	private StatusCode code;
	
	public Response(Header header, Body body, StatusCode code) {
		this.header = header;
		this.body = body;
		this.code = code;
	}
	
	public Header getHeader() {
		return header;
	}
	
	public Body getBody() {
		return body;
	}
	
	public StatusCode getCode() {
		return code;
	}
}
