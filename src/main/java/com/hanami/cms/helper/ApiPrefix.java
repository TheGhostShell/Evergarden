package com.hanami.cms.helper;

public enum ApiPrefix {
	v1Public("/v1"), v1Private("/v1/private");
	
	private String prefix;
	
	ApiPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	public String getPrefix() {
		return prefix;
	}
}
