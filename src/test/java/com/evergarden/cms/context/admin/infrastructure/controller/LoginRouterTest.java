package com.evergarden.cms.context.admin.infrastructure.controller;

import com.evergarden.cms.context.publisher.infrastructure.controller.PostHandler;
import com.evergarden.cms.context.publisher.infrastructure.controller.PostRouter;
import com.evergarden.cms.context.publisher.infrastructure.persistence.PostRepository;
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

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@WebFluxTest
class LoginRouterTest {
	
	@Autowired
	private Environment env;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@MockBean
	private PostRepository postRepository;
	
	private WebTestClient client;
	
	@BeforeEach
	void setUp() {
		PostHandler    postHandler = new PostHandler(logger, postRepository);
		RouterFunction router      = (new PostRouter()).postRoute(postHandler, env);
		client = WebTestClient.bindToRouterFunction(router)
			.build();
	}
	
	@Test
	void login() {
	
	}
}