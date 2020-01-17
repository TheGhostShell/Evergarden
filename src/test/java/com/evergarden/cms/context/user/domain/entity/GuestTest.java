package com.evergarden.cms.context.user.domain.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GuestTest {
    
    @Test
    void createInstance() {
        
        Guest guest = new Guest();
        guest.setToken("weak-token");
        guest.setSubject("violet@mail.com");
        
        assertEquals("weak-token", guest.getToken());
        assertEquals("violet@mail.com", guest.getSubject());
    }
}