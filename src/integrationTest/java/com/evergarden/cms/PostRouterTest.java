//package com.evergarden.cms;
//
//import com.evergarden.cms.context.publisher.application.service.CRUDPostService;
//import com.evergarden.cms.context.publisher.domain.entity.Post;
//import com.evergarden.cms.context.publisher.infrastructure.controller.PostHandler;
//import com.evergarden.cms.context.publisher.infrastructure.controller.PostRouter;
//import com.evergarden.cms.context.publisher.infrastructure.persistence.PostRepository;
//import com.evergarden.cms.context.user.infrastructure.controller.input.UnAuthUser;
//import io.restassured.RestAssured;
//import io.restassured.builder.RequestSpecBuilder;
//import io.restassured.specification.RequestSpecification;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.BDDMockito;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.core.env.Environment;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.springframework.test.web.reactive.server.WebTestClient;
//import org.springframework.web.reactive.function.server.RouterFunction;
//import org.testcontainers.shaded.com.google.common.net.HttpHeaders;
//import reactor.core.publisher.Mono;
//
///**
// * TODO add none nominal test case like onError->handle with ...
// * TODO security check and token validation
// */
//@ExtendWith(SpringExtension.class)
////@WebFluxTest
////@SpringBootTest
////@ContextConfiguration(classes={ReactiveMongoRepository.class})
////@AutoConfigureWebFlux
////@AutoConfigureWebTestClient
////@AutoConfigureDataMongo
////@EnableAutoConfiguration
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
//class PostRouterTest extends IntegrationCmsApplicationTests {
//
//    @Autowired
//    private Environment env;
//
//    private Logger logger = LoggerFactory.getLogger(this.getClass());
//
//    @MockBean
//    private CRUDPostService crudPostService;
//
//    @Autowired
//    private CRUDPostService crudPostServiceReal;
//
//    @Autowired
//    private PostRepository postRepository;
//
//    private WebTestClient client;
//
//    @BeforeEach
//    void setUp() {
//        PostHandler    postHandler = new PostHandler(crudPostService);
//        RouterFunction router      = (new PostRouter()).postRoute(postHandler, env);
//        client = WebTestClient.bindToRouterFunction(router)
//            .build();
//    }
//
//    @Test
//    void read() {
//
//        Post expectedPost = new Post();
//        expectedPost.setAuthor("john");
//        expectedPost.setTitle("<h1>The book ever</h1>");
//        expectedPost.setBody("<b>This is a wonderfull book I ever read</b>");
//        expectedPost.setId("postId");
//
//        BDDMockito.given(crudPostService.findById("postId"))
//            .willReturn(Mono.just(expectedPost));
//
//        client.get()
//            .uri(env.getProperty("v1") + "/post/{id}", "postId")
//            .accept(MediaType.APPLICATION_JSON)
//            .exchange()
//            .expectStatus().isOk()
//            .expectBody(Post.class)
//            .isEqualTo(expectedPost);
//    }
//
//    /*@Test
//    void show() {
//
//        UpdatedPost expectedPost1 = new UpdatedPost();
//        expectedPost1.setAuthor("john");
//        expectedPost1.setTitle("<h1>The book ever</h1>");
//        expectedPost1.setBody("<b>This is a wonderfull book I ever read</b>");
//        expectedPost1.setId(1L);
//
//        UpdatedPost expectedPost2 = new UpdatedPost();
//        expectedPost2.setAuthor("john2");
//        expectedPost2.setTitle("<h1>The book ever 2</h1>");
//        expectedPost2.setBody("<b>This is a wonderfull book I ever read 2</b>");
//        expectedPost2.setId(2L);
//
//        UpdatedPost expectedPost3 = new UpdatedPost();
//        expectedPost3.setAuthor("john3");
//        expectedPost3.setTitle("<h1>The book ever 3</h1>");
//        expectedPost3.setBody("<b>This is a wonderfull book I ever read 3</b>");
//        expectedPost3.setId(3L);
//
//        BDDMockito.given(crudPostService.fetchAll())
//            .willReturn(Flux.just(expectedPost1, expectedPost2, expectedPost3));
//
//        client.get()
//            .uri(env.getProperty("v1") + "/post")
//            .accept(MediaType.APPLICATION_JSON)
//            .exchange()
//            .expectStatus().isOk()
//            .expectBodyList(UpdatedPost.class)
//            .hasSize(3)
//            .contains(expectedPost3)
//            .contains(expectedPost1);
//    }*/
//
//    /*@Test
//    void delete() {
//
//        BDDMockito.given(crudPostService.delete(1))
//            .willReturn(Mono.empty());
//
//        client.delete()
//            .uri(env.getProperty("v1s") + "/post/{id}", 1)
//            .exchange()
//            .expectStatus().isOk();
//    }*/
//
//    @Test
//    void create() {
//
//        Post post             = Post.builder()
//            .body("Lorem ipsum")
//            .author("john")
//            .title("Best post")
//            .build();
//        // Post postMonoResponse = new Post("Best post", "Lorem ipsum", "john");
//        // postMonoResponse.setId(1L);
//
//        //crudPostServiceReal.create(post);
//
//        PostRequestTest request = new PostRequestTest(
//            "Lorem ipsum",
//            "john",
//            "Best post"
//        );
//
//        RequestSpecification requestSpecification = new RequestSpecBuilder()
//            .setPort(8080)
//            .addHeader(
//                HttpHeaders.CONTENT_TYPE,
//                MediaType.APPLICATION_JSON_VALUE
//            )
//            .build();
//
//        RestAssured.given(requestSpecification)
//            .request()
//            .body(UnAuthUser.builder().email("violet@mail.com").password("pass").build())
//            .when()
//            .post("/api/v1/login")
//            .then()
//            .statusCode(200)
//            .log();//.ifValidationFails(LogDetail.ALL);
//
//        /*client.post()
//            .uri(env.getProperty("v1s") + "/post")
//            .bodyValue(request)
//            .exchange()
//            .expectStatus().isOk()
//            .expectBody()
//            .jsonPath("body").isEqualTo("Lorem ipsum")
//            .jsonPath("author").isEqualTo("john")
//            .jsonPath("title").isEqualTo("Best post");
//            //.jsonPath("id").isEqualTo(1);*/
//    }
//
//    /*@Test
//    void update() {
//
//        UpdatedPost post = new UpdatedPost();
//        post.setBody("Lorem ipsum");
//        post.setTitle("Best post");
//        post.setAuthor("john");
//        post.setId(1L);
//
//        BDDMockito.given(crudPostService.update(post))
//            .willReturn(Mono.just(post));
//
//        PostRequestTest request = new PostRequestTest(
//            "Lorem ipsum",
//            "john",
//            "Best post"
//        );
//
//        request.setId(1L);
//
//        client.patch()
//            .uri(env.getProperty("v1s") + "/post/{id}", 1)
//            .syncBody(request)
//            .exchange()
//            .expectStatus().isOk()
//            .expectBody()
//            .jsonPath("body").isEqualTo("Lorem ipsum")
//            .jsonPath("author").isEqualTo("john")
//            .jsonPath("title").isEqualTo("Best post")
//            .jsonPath("id").isEqualTo(1);
//    }*/
//}