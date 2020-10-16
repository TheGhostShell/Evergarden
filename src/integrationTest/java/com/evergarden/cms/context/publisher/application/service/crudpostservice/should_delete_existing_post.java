package com.evergarden.cms.context.publisher.application.service.crudpostservice;

import com.evergarden.cms.IntegrationCmsApplicationTests;
import com.evergarden.cms.context.publisher.application.service.CRUDPostService;
import com.evergarden.cms.context.publisher.domain.entity.Post;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;


public class should_delete_existing_post extends IntegrationCmsApplicationTests {

    @Autowired
    private CRUDPostService crudPostService;

    @Test
    public void should_delete_previous_post_in_mongodb() {
        Post freshPost1 = Post.builder()
            .title("Nice title")
            .authorName("Violet")
            .body("I'm a tragic novel author")
            .build();

        StepVerifier.create(crudPostService.create(freshPost1))
            .expectNextMatches(post1 -> {
                Assertions.assertNotNull(post1.getId());
                return true;
            })
            .expectComplete()
            .verify();

        crudPostService.findAll()
            .flatMap(post -> crudPostService.deleteById(post.getId()))
            .blockLast();

        StepVerifier.create(crudPostService.findAll())
            .expectNextCount(0)
            .expectComplete()
            .verify();
    }
}
