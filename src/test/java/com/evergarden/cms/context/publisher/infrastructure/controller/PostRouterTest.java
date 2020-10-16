package com.evergarden.cms.context.publisher.infrastructure.controller;

import com.evergarden.cms.app.config.security.JwtHelper;
import com.evergarden.cms.context.publisher.application.service.CRUDPostService;
import com.evergarden.cms.context.publisher.domain.entity.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * TODO add none nominal test case like onError->handle with ...
 * TODO security check and token validation
 */
@ExtendWith(SpringExtension.class)
@WebFluxTest
@ContextConfiguration(classes = {PostRouter.class, PostHandler.class})
class PostRouterTest {

    @Autowired
    private Environment env;

    @MockBean
    private CRUDPostService crudPostService;

    @MockBean
    private JwtHelper jwtHelper;

    private WebTestClient client;

    @BeforeEach
    void setUp() {
        PostHandler                    postHandler = new PostHandler(crudPostService, jwtHelper, fastCacheUser);
        RouterFunction<ServerResponse> router      = (new PostRouter()).postRoute(postHandler, env);
        client = WebTestClient.bindToRouterFunction(router).build();
    }

    @Test
    void read() {

        Post expectedPost = new Post();
        expectedPost.setAuthorName("johnId");
        expectedPost.setTitle("<h1>The book ever</h1>");
        expectedPost.setBody("<b>This is a wonderfull book I ever read</b>");
        expectedPost.setId("postId");

        BDDMockito.given(crudPostService.findById("postId"))
            .willReturn(Mono.just(expectedPost));

        client.get()
            .uri(env.getProperty("v1") + "/post/{id}", "postId")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody(Post.class)
            .isEqualTo(expectedPost);
    }

    @Test
    void show() {

        Post expectedPost1 = new Post();
        expectedPost1.setAuthorName("johnId");
        expectedPost1.setTitle("<h1>The book ever</h1>");
        expectedPost1.setBody("<b>This is a wonderfull book I ever read</b>");
        expectedPost1.setId("post1");

        Post expectedPost2 = new Post();
        expectedPost2.setAuthorName("john2");
        expectedPost2.setTitle("<h1>The book ever 2</h1>");
        expectedPost2.setBody("<b>This is a wonderfull book I ever read 2</b>");
        expectedPost2.setId("post2");

        Post expectedPost3 = new Post();
        expectedPost3.setAuthorName("john3");
        expectedPost3.setTitle("<h1>The book ever 3</h1>");
        expectedPost3.setBody("<b>This is a wonderfull book I ever read 3</b>");
        expectedPost3.setId("post3");

        BDDMockito.given(crudPostService.findAll())
            .willReturn(Flux.just(expectedPost1, expectedPost2, expectedPost3));

        client.get()
            .uri(env.getProperty("v1") + "/post")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Post.class)
            .hasSize(3)
            .contains(expectedPost3)
            .contains(expectedPost1);
    }

    @Test
    void delete() {

        BDDMockito.given(crudPostService.deleteById("postToDelete")).willReturn(Mono.empty());

        client.delete()
            .uri(env.getProperty("v1s") + "/post/{id}", "postToDelete")
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    void create() {

        Post post = Post.builder()
            .body("Lorem ipsum")
            .authorName("john")
            .title("Best post")
            .build();
        Post postMonoResponse = Post.builder()
            .body("Lorem ipsum")
            .authorName("john")
            .title("Best post")
            .id("postId")
            .build();

        Post request = Post.builder()
            .title("Best post")
            .authorName("john")
            .body("Lorem ipsum")
            .build();

        BDDMockito.given(crudPostService.create(post))
            .willReturn(Mono.just(postMonoResponse));

        client.post()
            .uri(env.getProperty("v1s") + "/post")
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("body").isEqualTo("Lorem ipsum")
            .jsonPath("author").isEqualTo("john")
            .jsonPath("title").isEqualTo("Best post");
        //.jsonPath("id").isEqualTo(1);
    }

    @Test
    void update() {

        Post post = Post.builder()
            .body("Opo dum opo dim")
            .authorName("Mike")
            .title("The snake")
            .id("postId")
            .build();

        BDDMockito.given(crudPostService.updatePost(post))
            .willReturn(Mono.just(post));

        Post request = Post.builder()
            .body("Opo dum opo dim")
            .authorName("Mike")
            .title("The snake")
            .id("postId")
            .build();

        client.patch()
            .uri(env.getProperty("v1s") + "/post")
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("body").isEqualTo("Opo dum opo dim")
            .jsonPath("author").isEqualTo("Mike")
            .jsonPath("title").isEqualTo("The snake")
            .jsonPath("id").isEqualTo("postId");
    }
}