package com.evergarden.cms.context.user.application.security;

import com.evergarden.cms.app.config.security.JwtRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtRequestTest {

    @Test
    void createInstance() {
        JwtRequest jwtRequest = new JwtRequest("secret");
        assertEquals("secret", jwtRequest.getJwtSecret());
    }
}