package com.evergarden.cms.context.user.application.mapper;

import com.evergarden.cms.context.user.domain.entity.EncodedCredential;
import com.evergarden.cms.context.user.domain.entity.Profile;
import com.evergarden.cms.context.user.domain.entity.User;
import com.evergarden.cms.context.user.infrastructure.controller.input.ProfileSearch;
import com.evergarden.cms.context.user.infrastructure.controller.input.UpdatedUser;

import java.util.Optional;

public class UpdateUserMapper {
    // TODO need to be well tested
    public static User toUser(UpdatedUser updatedUser, User userFromDb) {
        return User.builder()
            .pseudo(defaultTo(updatedUser.getPseudo(), userFromDb.getPseudo()))
            .firstname(defaultTo(updatedUser.getFirstname(), userFromDb.getFirstname()))
            .lastname(defaultTo(updatedUser.getLastname(), userFromDb.getLastname()))
            .email(defaultTo(updatedUser.getEmail(), userFromDb.getEmail()))
            .activated(updatedUser.isActivated()) // TODO if not specified user will be deactivated ?
            .profile(Optional.ofNullable(mapToProfile(updatedUser.getProfile())).orElse(userFromDb.getProfile()))
            .encodedCredential(new EncodedCredential(userFromDb.getSalt(), userFromDb.getPassword()))
            .avatar(userFromDb.getAvatar())
            .id(userFromDb.getId())
            .build();
    }

    private static String defaultTo(String expected, String defaultValue) {
        return Optional.ofNullable(expected).orElse(defaultValue);
    }

    private static Profile mapToProfile(ProfileSearch profileSearch){
        return Optional.ofNullable(profileSearch)
            .map(profileSearch1 -> Profile.builder()
                .name(profileSearch1.getName())
                .id(profileSearch1.getId())
                .build())
            .orElse(null);
    }
}
