package com.hanami.sdk.router;

public class Path {
	private  String path;
	
	public Path(String path) {
		this.path = path;
	}
	
	@Override
	public String toString() {
		return path;
	}
}
