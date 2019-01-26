package com.evergarden.cms.context.admin.infrastructure.controller;

import com.evergarden.cms.context.admin.domain.security.EvergardenEncoder;
import com.evergarden.cms.context.admin.infrastructure.persistence.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;

@ExtendWith(SpringExtension.class)
@WebFluxTest
class LoginRouterTest {

    @Autowired
    private Environment env;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private LoginHandler   loginHandler;

    private RouterFunction router;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EvergardenEncoder encoder;

    @Autowired
    private WebTestClient client;

    @BeforeEach
    void setUp() {
        loginHandler = new LoginHandler(userRepository, logger, objectMapper, encoder, env);
        router = (new LoginRouter()).loginRoute(loginHandler, env);
        client = WebTestClient.bindToRouterFunction(router).build();
    }

//    @Test
//    void login() {
//
//    }
}