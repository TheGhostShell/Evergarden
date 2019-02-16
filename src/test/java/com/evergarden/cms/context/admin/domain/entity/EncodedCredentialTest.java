package com.evergarden.cms.context.admin.domain.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EncodedCredentialTest {
	
	@Test
	void testGetter() {
		EncodedCredential encodedCredential = new EncodedCredential("salt", "password");
		
		assertEquals("salt", encodedCredential.getSalt());
		assertEquals("password", encodedCredential.getEncodedPassword());
		
	}
}