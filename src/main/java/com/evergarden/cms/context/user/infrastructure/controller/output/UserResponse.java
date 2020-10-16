package com.evergarden.cms.context.user.infrastructure.controller.output;

import com.evergarden.cms.context.user.domain.entity.Avatar;
import com.evergarden.cms.context.user.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private String        id;
    private String        pseudo;
    private String        email;
    private String        firstname;
    private String        lastname;
    private String        avatarUrl;
    private boolean       activated;
    private ProfileResponse profile;

    public static UserResponse mapToUserResponse(User user) {

        UserResponse us = new UserResponse();

        us.setEmail(user.getEmail());
        us.setFirstname(user.getFirstname());
        us.setLastname(user.getLastname());
        us.setId(user.getId());
        us.setPseudo(user.getPseudo());
        us.setActivated(user.isActivated());
        us.setAvatarUrl(Optional.ofNullable(user.getAvatar())
            .map(Avatar::getRelativeUri)
            .orElse(""));
        us.setProfile(ProfileResponse.builder()
            .id(user.getProfile().getId())
            .name(user.getProfile().getName())
            .build());

        return us;
    }
}
