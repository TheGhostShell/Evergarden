package com.hanami.cms.context.admin.application.service;

import com.hanami.cms.context.admin.infrastructure.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateUserService {
	
	private UserRepository userRepository;
	
	@Autowired
	public CreateUserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	
}
