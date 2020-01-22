package com.evergarden.cms.context.user.infrastructure.controller.output;

import com.evergarden.cms.context.user.domain.entity.Avatar;
import com.evergarden.cms.context.user.domain.entity.Role;
import com.evergarden.cms.context.user.domain.entity.User;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Data
public class UserResponse {

    private String id;

    private String pseudo;

    private String email;

    private String firstname;

    private String lastname;

    private String avatarUrl;

    private boolean activated;

    private Collection<Role> roles = new ArrayList<>();

    public UserResponse addRole(Role role) {
        roles.add(role);
        return this;
    }

    public static UserResponse mapToUserResponse(User user) {

        UserResponse us = new UserResponse();

        us.setEmail(user.getEmail());
        us.setFirstname(user.getFirstname());
        us.setLastname(user.getLastname());
        us.setId(user.getId());
        us.setPseudo(user.getPseudo());
        us.setActivated(user.isActivated());
        us.setAvatarUrl(Optional.ofNullable(user.getAvatar()).map(Avatar::getRelativeUri).orElse(""));
        us.setRoles(user.getRoles());

        return us;
    }
}
