package com.evergarden.cms.context.user.infrastructure.controller.input;

import com.evergarden.cms.context.user.domain.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UnSaveUser {

    private boolean activated;
    private String email;
    private String firstname;
    private String lastname;
    private String pseudo;
    private String password;
    private Collection<Role> roles;
}
