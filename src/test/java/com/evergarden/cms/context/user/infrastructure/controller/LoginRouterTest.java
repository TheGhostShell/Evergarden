package com.evergarden.cms.context.user.infrastructure.controller;

import com.evergarden.cms.context.user.domain.entity.*;
import com.evergarden.cms.app.config.security.EvergardenEncoder;
import com.evergarden.cms.app.config.security.JwtHelper;
import com.evergarden.cms.app.config.security.JwtRequest;
import com.evergarden.cms.context.user.infrastructure.controller.input.UnAuthUser;
import com.evergarden.cms.context.user.infrastructure.controller.input.UpdatedUser;
import com.evergarden.cms.context.user.infrastructure.controller.output.UserCreateResponse;
import com.evergarden.cms.context.user.infrastructure.controller.output.UserResponse;
import com.evergarden.cms.context.user.infrastructure.persistence.UserRepository;
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
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collection;

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
        loginHandler = new LoginHandler(userRepository, roleRepository, logger, objectMapper, encoder, env, jwtHelper, generateTokenService, createUserService, generateGuestTokenService);
        router = (new LoginRouter()).loginRoute(loginHandler, env);
        client = WebTestClient.bindToRouterFunction(router).build();
    }

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

    @Test
    void guest() {
        Guest guest = new Guest();
        guest.setSubject("batou@mail.com");
        ArrayList<SimpleGrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority(new Role("guest").getRoleValue()));

        client.post()
            .uri(env.getProperty("v1") + "/guest")
            .syncBody(guest)
            .exchange()
            .expectStatus().isOk()
            .expectBody(Guest.class)
            .consumeWith(guestEntityExchangeResult -> {
                Guest g = guestEntityExchangeResult.getResponseBody();
                assertEquals("batou@mail.com", jwtHelper.getEmailFromToken(g.getToken()));
                assertEquals(roles, jwtHelper.getRolesFromToken(g.getToken()));
                assertTrue(jwtHelper.verifyToken(g.getToken()));
            });
    }

    @Test
    void read() {
        Collection<Role> roles = new ArrayList<>();
        Role             r1    = new Role("user").setId(1);
        roles.add(r1);

        User user = new User();
        user.setEmail("batou@mail.com");
        user.setFirstname("Batou");
        user.setLastname("Ranger");
        user.setPseudo("Batou");
        user.addRole(r1);
        user.setId(1);

        BDDMockito.given(userRepository.findById(1))
            .willReturn(Mono.just(user));

        client.get()
            .uri(env.getProperty("v1s") + "/user/{id}", 1)
            .exchange()
            .expectStatus().isOk()
            .expectBody(UserResponse.class)
            .consumeWith(userResponseEntityExchangeResult -> {
                UserResponse userR = userResponseEntityExchangeResult.getResponseBody();
                assertEquals("batou@mail.com", userR.getEmail());
                assertEquals("Batou", userR.getFirstname());
                assertEquals("Ranger", userR.getLastname());
                assertEquals("Batou", userR.getPseudo());
                assertEquals(1, userR.getId());
                assertNotNull(userR.getRoles());
            });
    }

    @Test
    void create() {
        ArrayList<String> roles = new ArrayList<>();
        roles.add("ROLE_COFFEE_MAKER");

        String data = "{\n" +
            "\t\"user\": {\n" +
            "\t\t\"firstname\":\"Batou\",\n" +
            "\t\t\"lastname\": \"Ranger\",\n" +
            "\t\t\"pseudo\": \"Batou\",\n" +
            "\t\t\"email\": \"batou@mail.com\",\n" +
            "\t\t\"password\": \"pass\",\n" +
            "\t\t\"roles\":[\n" +
            "\t\t\t\"COFFEE_MAKER\"\n" +
            "\t\t]\n" +
            "\t}\n" +
            "}";

        DataBuffer dataBuffer = (new DefaultDataBufferFactory()).wrap(data.getBytes());

        encoder.encode("pass");

        User userToSave = new User();

        userToSave.setEmail("batou@mail.com")
            .setFirstname("Batou")
            .setLastname("Ranger")
            .setPseudo("Batou")
            .setActivated(true)
            .addRole(new Role("admin"))
            .setEncodedCredential(encoder.getEncodedCredential());

        BDDMockito.given(userRepository.create(any(User.class))).willReturn(Mono.just(1));

        userToSave.setId(1);

        BDDMockito.given(userRepository.findById(1)).willReturn(Mono.just(userToSave));

        client.post()
            .uri(env.getProperty("v1s") + "/user")
            .syncBody(dataBuffer)
            .exchange()
            .expectBody(UserCreateResponse.class)
            .consumeWith(userCreateResponseEntityExchangeResult -> {
                UserCreateResponse user = userCreateResponseEntityExchangeResult.getResponseBody();
                assertEquals("batou@mail.com", user.getEmail());
                assertEquals("Batou", user.getFirstname());
                assertEquals("Ranger", user.getLastname());
                assertEquals("Batou", user.getPseudo());
                assertEquals(1, user.getId());
                assertEquals(roles, user.getRoles());
            });
    }

    // TODO some writing method don't need to return the new modified value
    @Test
    void update() {
        encoder.encode("pass");

        Collection<Role> roles = new ArrayList<>();
        roles.add(new Role("admin").setId(1));

        UpdatedUser updatedUser = new UpdatedUser(
            1,
            true,
            "batou@mail.com",
            "Batou",
            "Ranger",
            "Batou",
            "pass",
            roles
        );

        User user = new User();
        user.setId(1);
        user.setEmail("batou@mail.com");
        user.setFirstname("Batou");
        user.setLastname("Ranger");
        user.setPseudo("Batou");
        user.setActivated(true);
        user.setEncodedCredential(encoder.getEncodedCredential());
        user.addRole(new Role("admin").setId(1));

        BDDMockito.given(userRepository.update(any(UpdatedUser.class))).willReturn(Mono.just(user));

        client.put()
            .uri(env.getProperty("v1s") + "/user")
            .syncBody(updatedUser)
            .exchange()
            .expectStatus().isOk()
            .expectBody(UserResponse.class)
            .consumeWith(userCreateResponseExchangeResult -> {
                UserResponse us = userCreateResponseExchangeResult.getResponseBody();
                assertEquals("batou@mail.com", us.getEmail());
                assertEquals("Batou", us.getFirstname());
                assertEquals("Ranger", us.getLastname());
                assertEquals("Batou", us.getPseudo());
                assertEquals(1, us.getId());
            });
    }

    @Test
    void show() {
        User u1 = new User();
        u1.setId(1);
        u1.setEmail("batou@mail.com");
        u1.setFirstname("Batou");
        u1.setLastname("Ranger");
        u1.setPseudo("Batou");
        u1.setActivated(true);
        u1.addRole(new Role("admin").setId(1));

        User u2 = new User();
        u2.setId(1);
        u2.setEmail("denver@mail.com");
        u2.setFirstname("denver");
        u2.setLastname("dino");
        u2.setPseudo("denver");
        u2.setActivated(true);
        u2.addRole(new Role("writer").setId(2));

        User u3 = new User();
        u3.setId(1);
        u3.setEmail("motoko@mail.com");
        u3.setFirstname("motoko");
        u3.setLastname("kisanagi");
        u3.setPseudo("moto");
        u3.setActivated(true);
        u3.addRole(new Role("major").setId(3));

        BDDMockito.given(userRepository.fetchAll()).willReturn(Flux.just(u1, u2, u3));

        client.get()
            .uri(env.getProperty("v1s") + "/user")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(UserResponse.class)
            .consumeWith(listEntityExchangeResult -> {
                UserResponse ur1 = listEntityExchangeResult.getResponseBody().get(0);

                assertEquals("batou@mail.com", ur1.getEmail());
                assertEquals(1, ur1.getId());
                assertNotNull(ur1.getRoles());
                assertEquals(1, ur1.getRoles().toArray().length);
                assertEquals("Batou", ur1.getPseudo());
                assertEquals("Ranger", ur1.getLastname());
                assertEquals("Batou", ur1.getFirstname());
            })
            .hasSize(3);
    }
}