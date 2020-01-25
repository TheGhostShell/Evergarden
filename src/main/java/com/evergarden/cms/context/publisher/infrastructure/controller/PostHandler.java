package com.evergarden.cms.context.publisher.infrastructure.controller;

import com.evergarden.cms.context.publisher.application.service.CRUDPostService;
import com.evergarden.cms.context.publisher.domain.entity.Post;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class PostHandler {

    private CRUDPostService crudPostService;

    @Autowired
    public PostHandler(CRUDPostService crudPostService) {
        this.crudPostService = crudPostService;
    }

    public Mono<ServerResponse> create(ServerRequest request) {

        return request.body(BodyExtractors.toMono(Post.class)).flatMap(unSavedPost -> {

            Mono<Post> post = crudPostService.create(unSavedPost);

            return ServerResponse.ok().body(post, Post.class);
        });
    }

    public Mono<ServerResponse> read(ServerRequest request) {

        String id = request.pathVariable("id");

        return crudPostService
            .findById(id)
            .flatMap(post -> ServerResponse.ok().body(BodyInserters.fromValue(post)))
            .onErrorResume(throwable -> ServerResponse.badRequest()
                .body(BodyInserters.fromValue(throwable.getMessage())));
    }

    public Mono<ServerResponse> show(ServerRequest request) {

        return ServerResponse.ok().body(crudPostService.findAll(), Post.class)
            .onErrorResume(throwable -> ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> update(ServerRequest request) {

        return request
            .body(BodyExtractors.toMono(Post.class))
            .flatMap(crudPostService::updatePost)
            .flatMap(post -> ServerResponse.ok().body(BodyInserters.fromValue(post)))
            .onErrorResume(throwable -> ServerResponse.badRequest()
                .body(BodyInserters.fromValue(throwable.getMessage())));
    }

    public Mono<ServerResponse> delete(ServerRequest request) {

        String id = request.pathVariable("id");

        return crudPostService.deleteById(id)
            .flatMap(deletedPost -> ServerResponse.ok().build());
    }
}
