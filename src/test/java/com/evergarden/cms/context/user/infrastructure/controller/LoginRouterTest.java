package com.evergarden.cms.context.user.infrastructure.controller;

import com.evergarden.cms.app.config.security.EvergardenEncoder;
import com.evergarden.cms.app.config.security.JwtHelper;
import com.evergarden.cms.app.config.security.JwtRequest;
import com.evergarden.cms.context.publisher.infrastructure.persistence.PostRepository;
import com.evergarden.cms.context.user.application.service.GenerateGuestTokenService;
import com.evergarden.cms.context.user.application.service.GenerateTokenService;
import com.evergarden.cms.context.user.domain.entity.EncodedCredential;
import com.evergarden.cms.context.user.domain.entity.Guest;
import com.evergarden.cms.context.user.domain.entity.Profile;
import com.evergarden.cms.context.user.domain.entity.Role;
import com.evergarden.cms.context.user.domain.entity.Token;
import com.evergarden.cms.context.user.domain.entity.User;
import com.evergarden.cms.context.user.infrastructure.controller.input.UnAuthUser;
import com.evergarden.cms.context.user.infrastructure.persistence.ProfileRepository;
import com.evergarden.cms.context.user.infrastructure.persistence.RoleRepository;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import reactor.core.publisher.Mono;

import javax.cache.Cache;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @MockBean
    private PostRepository postRepository;

    @MockBean
    private RoleRepository roleRepository;

    @MockBean
    private ProfileRepository profileRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private EvergardenEncoder encoder;

    @Autowired
    private WebTestClient client;

    @MockBean
    private Cache<String, Token> tokenCache;

    @MockBean
    private JwtHelper jwtHelper;

    @MockBean
    private GenerateTokenService generateTokenService;

    @MockBean
    private GenerateGuestTokenService generateGuestTokenService;

    @BeforeEach
    void setUp() {
        // TODO refactor LoginHandler constructor parameter to simplify reduce number argument
        encoder = new EvergardenEncoder(env);
        jwtHelper = new JwtHelper(new JwtRequest(env.getProperty("jwt.secret")), tokenCache);
        loginHandler = new LoginHandler(generateTokenService, generateGuestTokenService);
        router = (new LoginRouter()).loginRoute(loginHandler, env);
        client = WebTestClient.bindToRouterFunction(router).build();
    }

    @Test
    void login() {
        UnAuthUser unAuthUser = new UnAuthUser("batou@mail.com", "pass");

        encoder = new EvergardenEncoder(env,
            new EncodedCredential("I3JycxGgTqJEQdD4VPeRcQ==", "F5w07xOZdOFNj3qqqikKP7Uhtj+b//O3dpWych1SbUg="));

        ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(new Role("test_admin").getRole()));
        ArrayList<Role> roles = new ArrayList<>();
        roles.add(new Role("test_admin"));

        User user = new User();
        user.setEmail("batou@mail.com");
        user.setFirstname("batou");
        user.setLastname("ranger");
        user.setPseudo("batou");
        user.setActivated(true);
        user.setProfile(Profile.builder().name("admin").roles(roles).build());
        user.setEncodedCredential(encoder.getEncodedCredential());

        BDDMockito.given(userRepository.findByEmail("batou@mail.com"))
            .willReturn(Mono.just(user));

        BDDMockito.given(generateTokenService.generateToken(unAuthUser))
            .willReturn(Mono.just(jwtHelper.generateToken("batou@mail.com", authorities,"1",
                Profile.builder().name("admin").build()))); //TODO to improve

        client.post()
            .uri(env.getProperty("v1") + "/login")
            .bodyValue(unAuthUser)
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
        Guest guest = Guest.builder().subject("batou@mail.com").build();
        ArrayList<SimpleGrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority(new Role("guest").getRole()));

        // TODO to improve
        Token token = jwtHelper.generateToken("batou@mail.com", roles, 1L, "", Profile.builder()
            .name("guest").build());

        BDDMockito.given(generateGuestTokenService.generateGuestToken(guest))
            .willReturn(Mono.just(Guest.builder().subject("batou@mail.com").token(token.getToken()).build()));

        client.post()
            .uri(env.getProperty("v1") + "/guest")
            .bodyValue(guest)
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
}