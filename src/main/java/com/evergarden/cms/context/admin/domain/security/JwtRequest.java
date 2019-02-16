package com.evergarden.cms.context.admin.domain.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class JwtRequest {

    @Getter
    private String jwtSecret;
}
