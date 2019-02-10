package com.evergarden.cms.context.admin.infrastructure.controller;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateUserRequest {
    private UserRequest userRequest;
}
