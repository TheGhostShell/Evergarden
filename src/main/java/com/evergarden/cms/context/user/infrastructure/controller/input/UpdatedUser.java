package com.evergarden.cms.context.user.infrastructure.controller.input;

import com.evergarden.cms.context.user.domain.entity.Avatar;
import com.evergarden.cms.context.user.domain.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatedUser {

    private String        id;
    private boolean       activated;
    private String        email;
    private String        firstname;
    private String        lastname;
    private String        pseudo;
    private String        avatarUrl;
    private Avatar        avatar;
    private ProfileSearch profile;
}
