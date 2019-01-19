package com.evergarden.cms.context.publisher.infrastructure.controller;

import com.evergarden.cms.context.publisher.domain.entity.UpdatedPost;
import com.evergarden.cms.context.publisher.infrastructure.persistence.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
@WebFluxTest
class PostRouterTest {
	
	@Autowired
	private Environment env;
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@MockBean
	PostRepository postRepository;
	
	@Test
	public void read() {
		
		UpdatedPost expectedPost = new UpdatedPost();
		expectedPost.setAuthor("john");
		expectedPost.setTitle("<h1>The book ever</h1>");
		expectedPost.setBody("<b>This is a wonderfull book I ever read</b>");
		expectedPost.setId(1L);
		
		BDDMockito.given(postRepository.fetchById(1L))
			.willReturn(Mono.just(expectedPost));
		
		PostHandler postHandler = new PostHandler(logger, postRepository);
		
		RouterFunction router = (new PostRouter()).postRoute(postHandler, env);
		
		WebTestClient client = WebTestClient.bindToRouterFunction(router).build();
		
		client.get()
			.uri(env.getProperty("v1") + "/post/{id}", 1)
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectBody(UpdatedPost.class)
			.isEqualTo(expectedPost);
	}
}