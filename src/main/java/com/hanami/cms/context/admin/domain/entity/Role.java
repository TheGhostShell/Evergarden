package com.hanami.cms.context.admin.domain.entity;

public enum Role {
	MASTER_ADMIN("ROLE_MASTER_ADMIN");
	
	private String role;
	
	Role(String role) {
		this.role = role;
	}
	
	public String toString() {
		return role;
	}
}
