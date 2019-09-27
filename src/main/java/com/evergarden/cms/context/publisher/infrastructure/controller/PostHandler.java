package com.evergarden.cms.context.publisher.infrastructure.controller;

import com.evergarden.cms.context.publisher.domain.entity.Post;
import com.evergarden.cms.context.publisher.domain.entity.PostMappingInterface;
import com.evergarden.cms.context.publisher.domain.entity.UpdatedPost;
import com.evergarden.cms.context.publisher.infrastructure.persistence.PostRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

        return request.body(BodyExtractors.toMono(UpdatedPost.class)).flatMap(updatedPost -> {

            Mono<Post> post = repository
                .save(new Post(
                    updatedPost.getTitle(),
                    updatedPost.getBody(),
                    updatedPost.getAuthor()
                ));

            return ServerResponse.ok().body(post, Post.class);
        });
    }

    public Mono<ServerResponse> read(ServerRequest request) {

        String id = request.pathVariable("id");

        return repository
            .findById(id)
            .onErrorReturn(Post.empty())
            .flatMap(PostHandler::handleEntityOrNotFound);
    }

    public Mono<ServerResponse> show(ServerRequest request) {

        Flux<Post> posts = repository.findAll();

        return ServerResponse.ok().body(posts, Post.class);
    }

    public Mono<ServerResponse> update(ServerRequest request) {

        Long id = Long.parseLong(request.pathVariable("id"));

        return request
            .body(BodyExtractors.toMono(Post.class))
            .flatMap(updatedPost -> {
                updatedPost.setId(id);
                return repository.save(updatedPost);
            })
            .onErrorReturn(Post.empty())
            .flatMap(PostHandler::handleEntityOrNotFound);
    }

    // TODO: 22/01/19 use long type for better code consistency
    public Mono<ServerResponse> delete(ServerRequest request) {

        String id = request.pathVariable("id");

        return repository.deleteById(id)
            .flatMap(deletedPost -> ServerResponse.ok().build());
    }

    private static Mono<ServerResponse> handleEntityOrNotFound(PostMappingInterface post) {
        if (post.getId() != 0) {
            return ServerResponse.ok().body(BodyInserters.fromObject(post));
        } else {
            return ServerResponse.notFound().build();
        }
    }
}
