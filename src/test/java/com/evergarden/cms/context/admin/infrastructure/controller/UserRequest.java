package com.evergarden.cms.context.admin.infrastructure.controller;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
public class UserRequest {
    private String            password;
    private String            lastname;
    private String            pseudo;
    private String            email;
    private String            firstname;
    private ArrayList<String> roles = new ArrayList<>();

}
