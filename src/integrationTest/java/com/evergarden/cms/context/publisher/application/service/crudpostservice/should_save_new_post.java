package com.evergarden.cms.context.publisher.application.service.crudpostservice;

import com.evergarden.cms.IntegrationCmsApplicationTests;
import com.evergarden.cms.context.publisher.application.service.CRUDPostService;
import com.evergarden.cms.context.publisher.domain.entity.Post;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;


public class should_save_new_post extends IntegrationCmsApplicationTests {

    @Autowired
    private CRUDPostService crudPostService;

    @Test
    public void contextLoadTest() {
    }

    @Test
    public void should_save_new_post_in_mongodb() {
        Post post = Post.builder()
            .title("Nice title")
            .authorId("7x34vioLEt7xc34")
            .body("I'm a writer")
            .build();

        StepVerifier.create(crudPostService.create(post))
            .expectNextMatches(post1 -> {
                Assertions.assertEquals("7x34vioLEt7xc34", post1.getAuthorId());
                Assertions.assertEquals("I'm a writer", post1.getBody());
                Assertions.assertEquals("Nice title", post1.getTitle());
                Assertions.assertNotNull(post1.getId());

                return true;
            })
            .expectComplete()
            .verify();

        crudPostService.findAll().flatMap(postToDelete -> crudPostService.deleteById(postToDelete.getId())).blockLast();
    }


}
