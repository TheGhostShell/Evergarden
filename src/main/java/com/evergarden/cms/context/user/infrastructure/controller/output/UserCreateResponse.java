package com.evergarden.cms.context.user.infrastructure.controller.output;

import com.evergarden.cms.context.user.domain.entity.User;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;

@Data
public class UserCreateResponse {
	
	private String id;
	
	private String pseudo;
	
	private String email;
	
	private String firstname;
	
	private String lastname;
	
	private Collection<String> roles = new ArrayList<>();
	
	public UserCreateResponse addRole(String role) {
		roles.add(role);
		return this;
	}
	
	public static UserCreateResponse mapToUserResponse(User user){
		
		UserCreateResponse us = new UserCreateResponse();
		
		us.setEmail(user.getEmail());
		us.setFirstname(user.getFirstname());
		us.setLastname(user.getLastname());
		us.setId(user.getId());
		us.setPseudo(user.getPseudo());
		
		user.getRoles().stream().peek(role -> {
			us.addRole(role.getRole());
		}).count();
		
		return us;
	}
	
	public UserCreateResponse dropRoles() {
		roles.clear();
		return this;
	}
}
