package com.evergarden.cms.context.publisher.infrastructure.controller;

import com.evergarden.cms.context.publisher.domain.entity.UpdatedPost;
import com.evergarden.cms.context.publisher.infrastructure.persistence.PostRepository;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * TODO add none nominal test case like onError->handle with ...
 * TODO security check and token validation
 */
@ExtendWith(SpringExtension.class)
@WebFluxTest
class PostRouterTest {
	
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
	public void read() {
		
		UpdatedPost expectedPost = new UpdatedPost();
		expectedPost.setAuthor("john");
		expectedPost.setTitle("<h1>The book ever</h1>");
		expectedPost.setBody("<b>This is a wonderfull book I ever read</b>");
		expectedPost.setId(1L);
		
		BDDMockito.given(postRepository.fetchById(1L))
			.willReturn(Mono.just(expectedPost));
		
		client.get()
			.uri(env.getProperty("v1") + "/post/{id}", 1)
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectBody(UpdatedPost.class)
			.isEqualTo(expectedPost);
	}
	
	@Test
	public void show() {
		
		UpdatedPost expectedPost1 = new UpdatedPost();
		expectedPost1.setAuthor("john");
		expectedPost1.setTitle("<h1>The book ever</h1>");
		expectedPost1.setBody("<b>This is a wonderfull book I ever read</b>");
		expectedPost1.setId(1L);
		
		UpdatedPost expectedPost2 = new UpdatedPost();
		expectedPost2.setAuthor("john2");
		expectedPost2.setTitle("<h1>The book ever 2</h1>");
		expectedPost2.setBody("<b>This is a wonderfull book I ever read 2</b>");
		expectedPost2.setId(2L);
		
		UpdatedPost expectedPost3 = new UpdatedPost();
		expectedPost3.setAuthor("john3");
		expectedPost3.setTitle("<h1>The book ever 3</h1>");
		expectedPost3.setBody("<b>This is a wonderfull book I ever read 3</b>");
		expectedPost3.setId(3L);
		
		BDDMockito.given(postRepository.fetchAll())
			.willReturn(Flux.just(expectedPost1, expectedPost2, expectedPost3));
		
		client.get()
			.uri(env.getProperty("v1") + "/post")
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectBodyList(UpdatedPost.class)
			.hasSize(3)
			.contains(expectedPost3)
			.contains(expectedPost1);
	}
	
	@Test
	public void delete() {
		
		BDDMockito.given(postRepository.delete(1))
			.willReturn(Mono.empty());
		
		client.delete()
			.uri(env.getProperty("v1s") + "/post/{id}", 1)
			.exchange()
			.expectStatus().isOk();
	}
	
	@Test
	public void create() {
	
	}
	
	@Test
	public void update() {
		
		UpdatedPost post = new UpdatedPost();
		post.setBody("Lorem ipsum");
		post.setTitle("Best post");
		post.setAuthor("john");
		post.setId(1L);
		
		BDDMockito.given(postRepository.update(post))
			.willReturn(Mono.just(post));
		
		PostRequestTest request = new PostRequestTest(
			1L,
			"Lorem ipsum",
			"john",
			"Best post"
		);
		
		client.patch()
			.uri(env.getProperty("v1s")+"/post/{id}", 1)
			.syncBody(request)
			.exchange()
			.expectStatus().isOk()
			.
	}
}