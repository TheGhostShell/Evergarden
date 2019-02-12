package com.evergarden.cms.context.admin.infrastructure.controller;

import com.evergarden.cms.context.admin.domain.entity.*;
import com.evergarden.cms.context.admin.domain.security.EvergardenEncoder;
import com.evergarden.cms.context.admin.domain.security.JwtHelper;
import com.evergarden.cms.context.admin.domain.security.JwtRequest;
import com.evergarden.cms.context.admin.infrastructure.controller.response.UserCreateResponse;
import com.evergarden.cms.context.admin.infrastructure.controller.response.UserResponse;
import com.evergarden.cms.context.admin.infrastructure.persistence.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
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

    UserRepository mockRep;

    @BeforeEach
    void setUp() {
        // TODO refactor LoginHandler constructor parameter to simplify reduce number argument
        encoder = new EvergardenEncoder(env, logger);
        mockRep = mock(UserRepository.class);
        jwtHelper = new JwtHelper(new JwtRequest(env.getProperty("jwt.secret")), logger);
        loginHandler = new LoginHandler(mockRep, logger, objectMapper, encoder, env, jwtHelper);
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
        roles.add("COFFEE_MAKER");

        Role targetRole = new Role("coffee_maker");
        targetRole.setId(1);

        String d = "{\n" +
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

        DataBuffer dataBuffer = (new DefaultDataBufferFactory()).wrap(d.getBytes());

        encoder.encode("pass");

        User userToSave = new User();

        userToSave.setEmail("batou@mail.com")
            .setFirstname("Batou")
            .setLastname("Ranger")
            .setPseudo("Batou")
            .setActivated(true)
            .addRole(new Role("admin"))
            .setEncodedCredential(encoder.getEncodedCredential());

        BDDMockito.given(userRepository.create(userToSave)).willReturn(Mono.just(new Integer("1")));
        BDDMockito.given(userRepository.createUserRole(targetRole, 1)).willReturn(Mono.just(targetRole));
        BDDMockito.given(userRepository.createRole(new Role("coffee_maker"))).willReturn(Mono.just(targetRole));
        BDDMockito.given(userRepository.findRole(new Role("coffee_maker"))).willReturn(Mono.just(targetRole));
        BDDMockito.given(userRepository.findById(1)).willReturn(Mono.just(userToSave));
        when(mockRep.create(any(User.class))).thenReturn(Mono.just(1));
        //doAnswer(Mono.just(1)).when(mockRep).create(userToSave);







        //userToSave.setId(1);

        //BDDMockito.given(userRepository.findById(2)).willReturn(Mono.just(userToSave));

        client.post()
            .uri(env.getProperty("v1s")+"/user")
            .syncBody(dataBuffer)
            .exchange()
            .expectBody(Integer.class)
            .consumeWith(userCreateResponseEntityExchangeResult -> {
                Integer user = userCreateResponseEntityExchangeResult.getResponseBody();
                assertEquals(1, user.intValue());
                //user.getEmail();
                //assertEquals("batou@mail.com", user.getEmail());
            });
    }
}