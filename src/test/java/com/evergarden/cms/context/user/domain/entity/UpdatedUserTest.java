package com.evergarden.cms.context.user.domain.entity;

import com.evergarden.cms.context.user.infrastructure.controller.input.UpdatedUser;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UpdatedUserTest {
    // Check assigned roles

    @Test
    void createInstanceAllParamConstructor() {
        Collection<Role> roles = new ArrayList<>();
        roles.add(new Role("ADMIN"));
        roles.add(new Role("GUEST"));

        Avatar avatar = new Avatar();

        UpdatedUser updatedUser = new UpdatedUser(
            "id",
            true,
            "violet@mail.com",
            "Batou",
            "Ranger",
            "Batou",
            "avatarUrl",
            avatar,
            roles
        );

        assertEquals("id", updatedUser.getId());
        assertEquals("violet@mail.com", updatedUser.getEmail());
        assertEquals("Batou", updatedUser.getFirstname());
        assertEquals("Ranger", updatedUser.getLastname());
        assertEquals("Batou", updatedUser.getPseudo());
        assertEquals(avatar, updatedUser.getAvatar());
        assertTrue(updatedUser.isActivated());
        assertEquals(roles, updatedUser.getRoles());
    }

    @Test
    void createInstanceWithDefaultConstructor() {
        Collection<Role> roles = new ArrayList<>();
        roles.add(new Role("ADMIN"));
        Avatar avatar = new Avatar();

        UpdatedUser updatedUser = new UpdatedUser();

        updatedUser.setEmail("violet@mail.com");
        updatedUser.setFirstname("Batou");
        updatedUser.setLastname("Ranger");
        updatedUser.setPseudo("Batou");
        updatedUser.setActivated(true);
        updatedUser.setId("id");
        updatedUser.setRoles(roles);
        updatedUser.setAvatar(avatar);

        assertEquals("id", updatedUser.getId());
        assertEquals("violet@mail.com", updatedUser.getEmail());
        assertEquals("Batou", updatedUser.getFirstname());
        assertEquals("Ranger", updatedUser.getLastname());
        assertEquals("Batou", updatedUser.getPseudo());
        assertEquals(avatar, updatedUser.getAvatar());
        assertTrue(updatedUser.isActivated());
        assertEquals(roles, updatedUser.getRoles());
    }

}