package com.evergarden.cms.context.user.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Credentials {

    private String login;

    private String password;
}
