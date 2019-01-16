package com.evergarden.cms.context.admin.infrastructure.controller.response;

import com.evergarden.cms.context.admin.domain.entity.Role;
import com.evergarden.cms.context.admin.domain.entity.UserMappingInterface;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;

@Data
public class UserResponse {
	
	private int id;
	
	private String pseudo;
	
	private String email;
	
	private String firstname;
	
	private String lastname;
	
	private Collection<Role> roles = new ArrayList<>();
	
	public UserResponse addRole(Role role) {
		roles.add(role);
		return this;
	}
	
	public static UserResponse mapToUserResponse(UserMappingInterface user) {
		
		UserResponse us = new UserResponse();
		
		us.setEmail(user.getEmail());
		us.setFirstname(user.getFirstName());
		us.setLastname(user.getLastName());
		us.setId(user.getId());
		us.setPseudo(user.getPseudo());
		
		us.setRoles(user.getRoles());
		
		return us;
	}
}
