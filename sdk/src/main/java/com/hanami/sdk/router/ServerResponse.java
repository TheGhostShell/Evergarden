package com.hanami.sdk.router;

public class ServerResponse {
	private Header     header;
	private Body       body;
	private StatusCode code;
	private String     fake;
	
	public ServerResponse(String fake) {
		this.fake = fake;
	}
	
	public String getFakeBodyResponse() {
		return "This is fake body response";
	}
	
	public String getFake() {
		return fake;
	}
}
