package com.hanami.cms.context.admin.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Credentials {

    private String login;

    private String password;
}
