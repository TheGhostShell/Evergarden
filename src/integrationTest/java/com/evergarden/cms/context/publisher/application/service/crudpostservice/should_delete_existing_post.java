package com.evergarden.cms.context.publisher.application.service.crudpostservice;

import com.evergarden.cms.context.publisher.application.service.CRUDPostService;
import com.evergarden.cms.context.publisher.domain.entity.Post;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class should_delete_existing_post {

    @Autowired
    private CRUDPostService crudPostService;

    @Test
    public void should_delete_previous_post_in_mongodb() {
        Post freshPost1 = Post.builder()
            .title("Nice title")
            .author("Violet")
            .body("I'm a tragic novel author")
            .build();

        crudPostService.create(freshPost1).block();

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
