package com.evergarden.cms.context.user.domain.entity;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testUserInstance() {
        User       user  = new User();
        List<Role> roles = new ArrayList<>();
        roles.add(new Role("admin"));

        Avatar avatar = Avatar.builder()
            .filePath("the/path")
            .fileName("avatar.jpeg")
            .relativeUri("relative/uri")
            .build();

        user.setEmail("batou@mail.com");
        user.setLastname("Ranger");
        user.setFirstname("Batou");
        user.setPseudo("Batou");
        user.setEncodedCredential(new EncodedCredential("salt", "password"));
        user.setActivated(true);
        user.setAvatar(avatar);
        user.setProfile(Profile.builder().roles(roles).name("admin").build());

        assertEquals("batou@mail.com", user.getEmail());
        assertEquals("Ranger", user.getLastname());
        assertEquals("Batou", user.getFirstname());
        assertEquals("Batou", user.getPseudo());
        assertEquals("salt", user.getSalt());
        assertEquals("password", user.getPassword());
        assertEquals("salt", user.getEncodedCredential().getSalt());
        assertEquals("password", user.getEncodedCredential().getEncodedPassword());
        assertTrue(user.isActivated());
        assertEquals(roles, user.getProfile().getRoles());

        assertEquals("the/path", user.getAvatar().getFilePath());
        assertEquals("avatar.jpeg", user.getAvatar().getFileName());
        assertEquals("relative/uri", user.getAvatar().getRelativeUri());
    }

    @Test
    void testUserBuilder() {

        List<Role> roles = new ArrayList<>();
        roles.add(new Role("admin"));

        User user = User.builder()
            .email("batou@mail.com")
            .lastname("Ranger")
            .firstname("Batou")
            .pseudo("Batou")
            .encodedCredential(new EncodedCredential("salt", "password"))
            .activated(true)
            .profile(Profile.builder().roles(roles).name("admin").build())
            .build();

        assertEquals("batou@mail.com", user.getEmail());
        assertEquals("Ranger", user.getLastname());
        assertEquals("Batou", user.getFirstname());
        assertEquals("Batou", user.getPseudo());
        assertEquals("salt", user.getSalt());
        assertEquals("password", user.getPassword());
        assertEquals("salt", user.getEncodedCredential().getSalt());
        assertEquals("password", user.getEncodedCredential().getEncodedPassword());
        assertTrue(user.isActivated());
        assertEquals(roles, user.getProfile().getRoles());
        assertTrue(user.getProfile().getRoles().size() > 0);
        assertNotNull(user.toString());
    }
}
