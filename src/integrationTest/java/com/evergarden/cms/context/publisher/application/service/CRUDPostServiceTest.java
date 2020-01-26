package com.evergarden.cms.context.publisher.application.service;

import com.evergarden.cms.IntegrationCmsApplicationTests;
import com.evergarden.cms.context.publisher.domain.entity.Post;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Objects;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class CRUDPostServiceTest extends IntegrationCmsApplicationTests {

    @Autowired
    private CRUDPostService crudPostService;

    @BeforeEach
    public void createOnePost() {
        crudPostService.create(
            Post.builder()
                .title("FirstPost")
                .author("violet")
                .body("My name is violet")
                .build()
        ).block();
    }

    @AfterEach
    public void deleteAllPost() {
        crudPostService.findAll()
            .map(post -> crudPostService.deleteById(post.getId()).subscribe())
            .blockLast();
    }


    @Test
    public void contextLoadTest(){}

    @Test
    public void should_save_new_post_in_mongodb() {
        Post post = Post.builder()
             .title("Nice title")
            .author("Violet")
            .body("I'm a writer")
            .build();

        StepVerifier.create(crudPostService.create(post))
            .expectNextMatches(post1 -> {
                Assertions.assertEquals("Violet", post1.getAuthor());
                Assertions.assertEquals("I'm a writer", post1.getBody());
                Assertions.assertEquals("Nice title", post1.getTitle());
                Assertions.assertNotNull(post1.getId());
                crudPostService.deleteById(post1.getId()).subscribe();
                return true;
            })
            .expectComplete()
            .verify();
    }

    @Test
    public void should_findById_new_post_in_mongodb() {
        Post unSavePost = Post.builder()
            .title("Nice title")
            .author("Violet")
            .body("I'm a writer")
            .build();

        StepVerifier.create(crudPostService.create(unSavePost).flatMap(post -> crudPostService.findById(post.getId())))
            .expectNextMatches(savedPost -> {
                Assertions.assertEquals("Violet", savedPost.getAuthor());
                Assertions.assertEquals("I'm a writer", savedPost.getBody());
                Assertions.assertEquals("Nice titl", savedPost.getTitle());
                Assertions.assertNotNull(savedPost.getId());
                crudPostService.deleteById(savedPost.getId()).subscribe();
                return true;
            })
            .expectComplete()
            .verify();
    }

    @Test
    public void should_findAll_post_in_mongodb() {
        Post freshPost1 = Post.builder()
            .title("Nice title")
            .author("Violet")
            .body("I'm a tragic novel author")
            .build();

        Post freshPost2 = Post.builder()
            .title("Nice title")
            .author("Violet")
            .body("I'm a detective novel author")
            .build();

        Flux.just(freshPost1, freshPost2)
            .flatMap(post -> Mono.just(Objects.requireNonNull(crudPostService.create(post).block())))
            .doOnComplete(() -> {
                StepVerifier.create(crudPostService.findAll())
                    .expectNextCount(1)
                    .assertNext(post -> {
                        Assertions.assertEquals("I'm a tragic novel author", post.getBody());
                        Assertions.assertNotNull(post.getId());
                    })
                    .assertNext(post -> {
                        Assertions.assertEquals("I'm a detective novel author", post.getBody());
                        Assertions.assertNotNull(post.getId());
                    })
                    .expectComplete()
                    .verify();
            })
            .subscribe();
    }
    @Test
    public void should_updated_previous_post_in_mongodb() {
        crudPostService.findAll()
            .flatMap(post -> {
                post.setAuthor("Motoko");
                post.setBody("I'm Mokoto izanagi");
                post.setTitle("Ghost");
                return crudPostService.updatePost(post);
            })
            .doOnComplete(() -> {
                StepVerifier.create(crudPostService.findAll())
                    .expectNextMatches(post -> {
                        Assertions.assertEquals("Motoko", post.getAuthor());
                        Assertions.assertEquals("I'm Mokoto izanagi", post.getBody());
                        Assertions.assertEquals("Ghost", post.getTitle());
                        return true;
                    })
                    .expectComplete()
                    .verify();
            })
            .subscribe();
    }

    @Test
    public void should_delete_previous_post_in_mongodb() {

        StepVerifier.create(crudPostService.findAll())
            .expectNextMatches(post1 -> {
                crudPostService.deleteById(post1.getId()).subscribe();
                return true;
            })
            .expectComplete()
            .verify();

        StepVerifier.create(crudPostService.findAll())
            .expectNextCount(0)
            .expectComplete()
            .verify();
    }
}
