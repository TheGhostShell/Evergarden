package com.evergarden.cms.app.config.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class JwtRequest {

    @Getter
    private String jwtSecret;
}
