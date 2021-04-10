package com.evergarden.cms.context.publisher.infrastructure.controller;

import com.evergarden.cms.app.config.security.JwtHelper;
import com.evergarden.cms.context.publisher.application.service.CRUDPostService;
import com.evergarden.cms.context.publisher.application.utils.FastCacheUser;
import com.evergarden.cms.context.publisher.domain.entity.Post;
import com.evergarden.cms.context.user.domain.entity.Token;
import com.evergarden.cms.context.user.domain.entity.TokenDecrypted;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * TODO add none nominal test case like onError->handle with ...
 * TODO security check and token validation
 * TODO review the tests because request value its not evaluated
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

    @MockBean
    private FastCacheUser fastCacheUser;

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
        expectedPost.setAuthorId("johnId");
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
        expectedPost1.setAuthorId("johnId");
        expectedPost1.setTitle("<h1>The book ever</h1>");
        expectedPost1.setId("post1");

        Post expectedPost2 = new Post();
        expectedPost2.setAuthorId("john2");
        expectedPost2.setTitle("<h1>The book ever 2</h1>");
        expectedPost2.setId("post2");

        Post expectedPost3 = new Post();
        expectedPost3.setAuthorId("john3");
        expectedPost3.setTitle("<h1>The book ever 3</h1>");
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

        Post postMonoResponse = Post.builder()
            .body("Lorem ipsum")
            .authorId("userId")
            .title("Best post")
            .id("postId")
            .summary("summary")
            .build();

        Post postReq = Post.builder()
            .build();

        BDDMockito.given(crudPostService.create(Mockito.any()))
            .willReturn(Mono.just(postMonoResponse));

        BDDMockito.given(jwtHelper.fromServerRequest(Mockito.any(ServerRequest.class)))
            .willReturn(TokenDecrypted.builder().userId("userId").build());

        client.post()
            .uri(env.getProperty("v1s") + "/post")
            .bodyValue(postReq)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("authorId").isEqualTo("userId")
            .jsonPath("summary").isEqualTo("summary")
            .jsonPath("title").isEqualTo("Best post");
        //.jsonPath("id").isEqualTo(1);
    }

    @Test
    void update() {

        Post post = Post.builder()
            .body("Opo dum opo dim")
            .authorId("MikeId2")
            .title("The snake")
            .id("postId")
            .build();

        BDDMockito.given(crudPostService.updatePost(Mockito.any(Post.class))).willReturn(Mono.just(post));

        Post request = Post.builder()
            .build();

        client.patch()
            .uri(env.getProperty("v1s") + "/post")
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("body").isEqualTo("Opo dum opo dim")
            .jsonPath("authorId").isEqualTo("MikeId2")
            .jsonPath("title").isEqualTo("The snake")
            .jsonPath("id").isEqualTo("postId");
    }
}
