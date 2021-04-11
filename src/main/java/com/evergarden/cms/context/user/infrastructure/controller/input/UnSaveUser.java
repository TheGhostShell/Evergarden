package com.evergarden.cms.context.user.infrastructure.controller.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UnSaveUser {

    private boolean       activated;
    private String        email;
    private String        firstname;
    private String        lastname;
    private String        pseudo;
    private String        password;
    @NotNull
    private ProfileSearch profile;
}
