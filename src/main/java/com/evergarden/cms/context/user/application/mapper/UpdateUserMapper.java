package com.evergarden.cms.context.user.application.mapper;

import com.evergarden.cms.context.user.domain.entity.User;
import com.evergarden.cms.context.user.infrastructure.controller.input.UpdatedUser;

import java.util.Optional;

public class UpdateUserMapper {
    public static User toUser(UpdatedUser updatedUser, User userFromDb) {
        return User.builder()
            .pseudo(defaultTo(updatedUser.getPseudo(), userFromDb.getPseudo()))
            .firstname(defaultTo(updatedUser.getFirstname(), userFromDb.getFirstname()))
            .lastname(defaultTo(updatedUser.getLastname(), userFromDb.getLastname()))
            .email(defaultTo(updatedUser.getEmail(), userFromDb.getEmail()))
            .activated(updatedUser.isActivated())
            .roles(Optional.ofNullable(updatedUser.getRoles()).orElse(userFromDb.getRoles()))
            .password(userFromDb.getPassword())
            .avatar(userFromDb.getAvatar())
            .salt(userFromDb.getSalt())
            .id(userFromDb.getId())
            .build();
    }

    private static String defaultTo(String expected, String defaultValue) {
        return Optional.ofNullable(expected).orElse(defaultValue);
    }
}
