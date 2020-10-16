package com.evergarden.cms.context.user.application.security;

import com.evergarden.cms.IntegrationCmsApplicationTests;
import com.evergarden.cms.app.config.security.EvergardenAuthenticationManager;
import com.evergarden.cms.app.config.security.JwtHelper;
import com.evergarden.cms.app.config.security.SecurityContextRepository;
import com.evergarden.cms.context.user.domain.entity.Profile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ServerWebExchange;
import reactor.test.StepVerifier;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class SecurityContextRepositoryTest extends IntegrationCmsApplicationTests {

    @Autowired
    JwtHelper jwtHelper;

    @Test
    void save() {
        EvergardenAuthenticationManager manager = new EvergardenAuthenticationManager(jwtHelper);
        SecurityContextRepository context = new SecurityContextRepository(manager, jwtHelper);

        ServerWebExchange mockServer  = mock(ServerWebExchange.class);
        SecurityContext   mockContext = mock(SecurityContext.class);

        assertThrows(UnsupportedOperationException.class, () -> context.save(mockServer, mockContext));
    }

    @Test
    void load() {

        EvergardenAuthenticationManager   manager = new EvergardenAuthenticationManager(jwtHelper);
        SecurityContextRepository         context = new SecurityContextRepository(manager, jwtHelper);
        ArrayList<SimpleGrantedAuthority> roles   = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

        // TODO to improve profile
        String token = jwtHelper.generateToken("batou@mail.com", roles, "userId", Profile.builder().build()).getToken();

        ServerHttpRequest mockRequest = mock(ServerHttpRequest.class);
        HttpHeaders       headers     = mock(HttpHeaders.class);
        ServerWebExchange mockServer  = mock(ServerWebExchange.class);

        when(mockServer.getRequest()).thenReturn(mockRequest);
        when(mockRequest.getHeaders()).thenReturn(headers);
        when(headers.getFirst(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);

        Authentication      userToken = new UsernamePasswordAuthenticationToken("batou@mail.com", token, roles);
        SecurityContextImpl sci       = new SecurityContextImpl(userToken);

        StepVerifier.create(context.load(mockServer))
            .expectNextMatches(securityContext -> {
                assertEquals(sci, securityContext);
                return true;
            })
            .verifyComplete();

        when(headers.getFirst(HttpHeaders.AUTHORIZATION)).thenReturn(token);
        StepVerifier.create(context.load(mockServer))
            .verifyComplete();
    }
}