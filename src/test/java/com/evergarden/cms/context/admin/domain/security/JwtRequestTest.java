package com.evergarden.cms.context.admin.domain.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtRequestTest {

    @Test
    void createInstance() {
        JwtRequest jwtRequest = new JwtRequest("secret");
        assertEquals("secret", jwtRequest.getJwtSecret());
    }
}