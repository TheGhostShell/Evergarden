package com.evergarden.cms.context.user.domain.entity;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EncodedCredential {

    private String salt;

    private String encodedPassword;

    public String getSalt() {
        return salt;
    }

    public String getEncodedPassword() {
        return encodedPassword;
    }
}
