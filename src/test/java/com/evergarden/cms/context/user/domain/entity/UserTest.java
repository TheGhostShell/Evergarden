package com.evergarden.cms.context.user.domain.entity;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testUserInstance() {
        User user = new User();
        Collection<Role> roles = new ArrayList<>();
        roles.add(new Role("admin"));

        user.setEmail("batou@mail.com");
        user.setLastname("Ranger");
        user.setFirstname("Batou");
        user.setPseudo("Batou");
        user.setEncodedCredential(new EncodedCredential("salt", "password"));
        user.setId(1);
        user.setActivated(true);
        user.addRole(new Role("admin"));

        assertEquals("batou@mail.com", user.getEmail());
        assertEquals("Ranger", user.getLastname());
        assertEquals("Batou", user.getFirstname());
        assertEquals("Batou", user.getPseudo());
        assertEquals("salt", user.getSalt());
        assertEquals("password", user.getPassword());
        assertEquals("batou@mail.com", user.getUsername());
        assertEquals("salt", user.getEncodedCredential().getSalt());
        assertEquals("password", user.getEncodedCredential().getEncodedPassword());
        //assertEquals(user.getRoles());
    }
}