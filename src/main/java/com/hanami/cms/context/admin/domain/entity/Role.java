package com.hanami.cms.context.admin.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Role {

    @Getter
    private int userId;

    @Getter
    private String role;
}
