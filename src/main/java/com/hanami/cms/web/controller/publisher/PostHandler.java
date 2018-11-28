package com.hanami.cms.web.controller.publisher;

import com.hanami.cms.entity.publisher.Post;
import com.hanami.cms.infrastructure.publisher.PostRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

@Component
public class PostHandler {

    private Logger logger;
    private PostRepository repository;

    @Autowired
    public PostHandler(Logger logger, PostRepository repository) {
        this.logger = logger;
        this.repository = repository;
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        
        Flux<Post> posts = repository.fetchAll();
        
        return ServerResponse.ok().body(posts, Post.class);
    }
    
    public Mono<ServerResponse> read(ServerRequest request) {
        
        int id = Integer.parseInt(request.pathVariable("id"));
        
        logger.info("Read request with id:"+ id);
        
        try {
            Mono<com.hanami.cms.entity.publisher.mapping.Post> post = repository.fetchById(id);
            
            return ServerResponse.ok().body(post, com.hanami.cms.entity.publisher.mapping.Post.class)
                .onErrorMap()
                .switchIfEmpty(ServerResponse.notFound().build());
        } catch (NoSuchElementException exception) {
            return  ServerResponse.notFound().build();
        }
    }
}
