package com.evergarden.cms.context.admin.domain.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CredentialsTest {
	
	@Test
	void testGetter() {
		Credentials  credentials = new Credentials("batou@mail.com", "password");
		assertEquals("batou@mail.com", credentials.getLogin());
		assertEquals("password", credentials.getPassword());
		
		credentials.setLogin("violet@mail.com");
		credentials.setPassword("pass");
		
		assertEquals("violet@mail.com", credentials.getLogin());
		assertEquals("pass", credentials.getPassword());
	}
	
}