package com.hanami.cms.context.admin.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatedUser {

    private int id;
    private boolean activated;
    private String email;
    private String firstname;
    private String lastname;
    private String pseudo;
    private String password;
    private Collection roles;
}
