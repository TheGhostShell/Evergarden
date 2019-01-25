package com.evergarden.cms.context.admin.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class UnAuthUser {
	
	@Getter
	private String email;
	
	@Getter
	private String password;
}
