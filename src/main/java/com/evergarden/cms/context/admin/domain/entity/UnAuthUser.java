package com.evergarden.cms.context.admin.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
public class UnAuthUser {

    private String email;

    private String password;

    public UnAuthUser() {
    }
}
