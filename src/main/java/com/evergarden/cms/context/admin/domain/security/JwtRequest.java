package com.evergarden.cms.context.admin.domain.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class JwtRequest {

    @Getter
    private String jwtSecret;
}
