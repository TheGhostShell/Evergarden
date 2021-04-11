package com.evergarden.cms.context.publisher.application.service.crudpostservice;

import com.evergarden.cms.IntegrationCmsApplicationTests;
import com.evergarden.cms.context.publisher.application.service.CRUDPostService;
import com.evergarden.cms.context.publisher.domain.entity.Post;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;


public class should_updated_previous_post extends IntegrationCmsApplicationTests {

    @Autowired
    private CRUDPostService crudPostService;

    @Test
    public void should_updated_previous_post_in_mongodb() {
        Post freshPost1 = Post.builder()
            .title("Nice title")
            .authorId("7x34vioLEt7xc34")
            .body("I'm a tragic novel author")
            .build();

        crudPostService.create(freshPost1).block();

        crudPostService.findAll()
            .flatMap(post -> {
                post.setTitle("Ghost");
                post.setBody("I'm Motoko izanagi");
                post.setAuthorId("Motoko");
                return crudPostService.updatePost(post);
            })
            .doOnComplete(() -> {
            })
            .blockLast();

        StepVerifier.create(crudPostService.findAll())
            .expectNextMatches(post -> {
                Assertions.assertEquals("Motoko", post.getAuthorId());
                Assertions.assertEquals("I'm Motoko izanagi", post.getBody());
                Assertions.assertEquals("Ghost", post.getTitle());
                return true;
            })
            .expectNextCount(0)
            .verifyComplete();

        crudPostService.findAll().flatMap(post -> crudPostService.deleteById(post.getId())).blockLast();
    }
}
