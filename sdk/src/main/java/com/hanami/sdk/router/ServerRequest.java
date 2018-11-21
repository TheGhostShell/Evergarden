package com.hanami.sdk.router;

public class ServerRequest {
	private Header          header;
	private Path            path;
	private QueryParameters query;
	private Body            body;

	public ServerRequest(Path path) {
		this.path = path;
	}
}
