package com.evergarden.cms.context.admin.domain.entity;

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