package com.evergarden.cms.context.admin.infrastructure.controller;

import com.evergarden.cms.context.admin.domain.entity.*;
import com.evergarden.cms.context.admin.domain.security.EvergardenEncoder;
import com.evergarden.cms.context.admin.domain.security.JwtHelper;
import com.evergarden.cms.context.admin.domain.security.JwtRequest;
import com.evergarden.cms.context.admin.infrastructure.persistence.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

@ExtendWith(SpringExtension.class)
@WebFluxTest
class LoginRouterTest {

    @Autowired
    private Environment env;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private LoginHandler loginHandler;

    private RouterFunction router;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private EvergardenEncoder encoder;

    @Autowired
    private WebTestClient client;

    private JwtHelper jwtHelper;

    @BeforeEach
    void setUp() {
        // TODO refactor LoginHandler constructor parameter to simplify reduce number argument
        encoder = new EvergardenEncoder(env, logger);
        jwtHelper = new JwtHelper(new JwtRequest(env.getProperty("jwt.secret")), logger);
        loginHandler = new LoginHandler(userRepository, logger, objectMapper, encoder, env, jwtHelper);
        router = (new LoginRouter()).loginRoute(loginHandler, env);
        client = WebTestClient.bindToRouterFunction(router).build();
    }

    /*
    Post post             = new Post("Best post", "Lorem ipsum", "john");
        Post postMonoResponse = new Post("Best post", "Lorem ipsum", "john");
        postMonoResponse.setId(1L);

        BDDMockito.given(postRepository.create(post))
            .willReturn(Mono.just(postMonoResponse));

        PostRequestTest request = new PostRequestTest(
            "Lorem ipsum",
            "john",
            "Best post"
        );

        client.post()
            .uri(env.getProperty("v1s") + "/post")
            .syncBody(request)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("body").isEqualTo("Lorem ipsum")
            .jsonPath("author").isEqualTo("john")
            .jsonPath("title").isEqualTo("Best post")
            .jsonPath("id").isEqualTo(1);
     */
    @Test
    void login() {
        UnAuthUser unAuthUser = new UnAuthUser("batou@mail.com", "pass");

        encoder = new EvergardenEncoder(env, logger, new EncodedCredential("salt", null));
        encoder.encode("pass");
        EncodedCredential enc = encoder.getEncodedCredential();

        User user = new User();
        user.setEmail("batou@mail.com");
        user.setFirstname("batou");
        user.setLastname("ranger");
        user.setPseudo("batou");
        user.setActivated(true);
        user.addRole(new Role("test_admin"));
        user.setEncodedCredential(new EncodedCredential(enc.getSalt(), enc.getEncodedPassword()));



        BDDMockito.given(userRepository.findByEmail("batou@mail.com"))
            .willReturn(Mono.just(user));

        client.post()
            .uri(env.getProperty("v1") + "/login")
            .syncBody(unAuthUser)
            .exchange()
            .expectStatus().isOk()
            .expectBody(Token.class)
            .consumeWith(tokenEntityExchangeResult -> {
                Token token = tokenEntityExchangeResult.getResponseBody();
                assertTrue(jwtHelper.verifyToken(token.getToken()));
            });
    }
}