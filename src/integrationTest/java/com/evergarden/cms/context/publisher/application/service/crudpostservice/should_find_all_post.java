package com.evergarden.cms.context.publisher.application.service.crudpostservice;

import com.evergarden.cms.IntegrationCmsApplicationTests;
import com.evergarden.cms.context.publisher.application.service.CRUDPostService;
import com.evergarden.cms.context.publisher.domain.entity.Post;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;


public class should_find_all_post extends IntegrationCmsApplicationTests {

    @Autowired
    private CRUDPostService crudPostService;

    @Test
    public void should_findAll_post_in_mongodb() {
        Post freshPost1 = Post.builder()
            .title("Nice title")
            .authorId("7x34vioLEt7xc34")
            .body("I'm a tragic novel author")
            .build();

        Post freshPost2 = Post.builder()
            .title("Nice title")
            .authorId("7x34vioLEt7xc36")
            .body("I'm a detective novel author")
            .build();

        Flux.just(freshPost1, freshPost2)
            .map(post -> crudPostService.create(post).subscribe())
            .doOnComplete(() -> {
                StepVerifier.create(crudPostService.findAll())
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
            .blockLast();
        crudPostService.findAll().doOnNext(post -> crudPostService.deleteById(post.getId())).blockLast();
    }
}
