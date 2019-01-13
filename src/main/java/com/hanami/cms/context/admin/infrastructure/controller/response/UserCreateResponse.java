package com.hanami.cms.context.admin.infrastructure.controller.response;

import com.hanami.cms.context.admin.domain.entity.UserMappingInterface;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;

@Data
public class UserCreateResponse {
	
	private int id;
	
	private String pseudo;
	
	private String email;
	
	private String firstname;
	
	private String lastname;
	
	private Collection<String> roles = new ArrayList<>();
	
	public UserCreateResponse addRole(String role) {
		roles.add(role);
		return this;
	}
	
	public static UserCreateResponse mapToUserResponse(UserMappingInterface user){
		
		UserCreateResponse us = new UserCreateResponse();
		
		us.setEmail(user.getEmail());
		us.setFirstname(user.getFirstName());
		us.setLastname(user.getLastName());
		us.setId(user.getId());
		us.setPseudo(user.getPseudo());
		
		user.getRoles().stream().peek(role -> {
			us.addRole(role.getRoleValue());
		}).count();
		
		return us;
	}
	
	public UserCreateResponse dropRoles() {
		roles.clear();
		return this;
	}
}
