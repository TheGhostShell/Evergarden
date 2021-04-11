package com.evergarden.cms.context.user.infrastructure.controller.output;

import com.evergarden.cms.context.user.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreateResponse {

    private String          id;
    private String          pseudo;
    private String          email;
    private String          firstname;
    private String          lastname;
    private ProfileResponse profile;

    public static UserCreateResponse mapToUserResponse(User user) {

        UserCreateResponse us = new UserCreateResponse();

        us.setEmail(user.getEmail());
        us.setFirstname(user.getFirstname());
        us.setLastname(user.getLastname());
        us.setId(user.getId());
        us.setPseudo(user.getPseudo());
        us.setProfile(ProfileResponse.builder()
			.id(user.getProfile().getId())
			.name(user.getProfile().getName())
			.build()
		);

        return us;
    }
}
