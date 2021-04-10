package com.evergarden.cms.context.user.domain.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProfileTest {

    @Test
    void setName() {
        Profile profile = new Profile();
        profile.setName("admin_Financial");

        Assertions.assertEquals("ADMIN_FINANCIAL", profile.getName());

        profile = Profile.builder()
            .name("FINANcial")
            .build();

        Assertions.assertEquals("FINANCIAL", profile.getName());
    }
}
