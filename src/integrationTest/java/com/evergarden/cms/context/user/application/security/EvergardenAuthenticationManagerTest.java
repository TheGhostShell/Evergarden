package com.evergarden.cms.context.user.application.security;

import com.evergarden.cms.IntegrationCmsApplicationTests;
import com.evergarden.cms.app.config.security.EvergardenAuthenticationManager;
import com.evergarden.cms.app.config.security.JwtHelper;
import com.evergarden.cms.context.user.domain.entity.Profile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class EvergardenAuthenticationManagerTest extends IntegrationCmsApplicationTests {

    @Autowired
    JwtHelper jwtHelper;

    @Test
    void authenticateFailedWithBadToken() {
        Authentication authentication = new UsernamePasswordAuthenticationToken("batou@mail.com", "XLqMAPeSDKERF");
        EvergardenAuthenticationManager manager = new EvergardenAuthenticationManager(jwtHelper);
        StepVerifier.create(manager.authenticate(authentication))
            .verifyComplete();
    }

    @Test
    void authenticatedSuccessWithGoodToken() {
        ArrayList<SimpleGrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        //TODO to improve profile
        String token = jwtHelper.generateToken("batou@mail.com", roles, "userId",
            Profile.builder()
                .name("fakeProfile")
                .build())
            .getToken();
        Authentication authentication = new UsernamePasswordAuthenticationToken("batou@mail.com", token);
        EvergardenAuthenticationManager manager = new EvergardenAuthenticationManager(jwtHelper);

        StepVerifier.create(manager.authenticate(authentication))
            .expectNextMatches(authentication1 -> {
                Authentication expected = new UsernamePasswordAuthenticationToken("batou@mail.com", token, roles);
                assertEquals(expected, authentication1);
                return true;
            })
            .verifyComplete();
    }
}