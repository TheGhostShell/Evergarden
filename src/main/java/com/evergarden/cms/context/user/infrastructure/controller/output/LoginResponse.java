package com.evergarden.cms.context.user.infrastructure.controller.output;

import lombok.Data;

@Data
public class LoginResponse {
    private String email;
    private String token;
}
