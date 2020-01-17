package com.evergarden.cms.context.user.domain.entity;

import com.evergarden.cms.context.user.infrastructure.controller.input.UnAuthUser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UnAuthUserTest {
	
	@Test
	void createInstance() {
		UnAuthUser user = new UnAuthUser("batou@mail.net", "pass");
		
		assertEquals("batou@mail.net", user.getEmail());
		assertEquals("pass", user.getPassword());
	}
	
}