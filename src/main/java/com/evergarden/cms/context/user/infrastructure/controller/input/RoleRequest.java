package com.evergarden.cms.context.user.infrastructure.controller.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class RoleRequest {
    private String id;
    private String role;
}
