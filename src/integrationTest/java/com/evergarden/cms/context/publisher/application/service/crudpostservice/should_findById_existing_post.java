package com.evergarden.cms.context.publisher.application.service.crudpostservice;

import com.evergarden.cms.IntegrationCmsApplicationTests;
import com.evergarden.cms.context.publisher.application.service.CRUDPostService;
import com.evergarden.cms.context.publisher.domain.entity.Post;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;


public class should_findById_existing_post extends IntegrationCmsApplicationTests {

    @Autowired
    private CRUDPostService crudPostService;

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
                Assertions.assertEquals("Nice title", savedPost.getTitle());
                Assertions.assertNotNull(savedPost.getId());
                return true;
            })
            .expectComplete()
            .verify();

        crudPostService.findAll().flatMap(postToDelete -> crudPostService.deleteById(postToDelete.getId())).blockLast();

    }
}
