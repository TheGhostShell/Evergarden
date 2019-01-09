package com.hanami.cms.context.admin.domain.entity;

import java.util.List;

public class UserRequest {
	
	private String lastname;
	
	private String firstname;
	
	private String email;
	
	private String pseudo;
	
	private boolean activated;
	
	private List<RoleEnum> roles;
	
	private String salt;
	
	
}
