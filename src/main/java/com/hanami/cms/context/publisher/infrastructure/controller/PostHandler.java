package com.hanami.cms.context.publisher.infrastructure.controller;

import com.hanami.cms.context.publisher.domain.entity.Post;
import com.hanami.cms.context.publisher.domain.entity.PostMappingInterface;
import com.hanami.cms.context.publisher.domain.entity.UpdatedPost;
import com.hanami.cms.context.publisher.infrastructure.persistence.PostRepository;
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

    private Logger         logger;
    private PostRepository repository;

    @Autowired
    public PostHandler(Logger logger, PostRepository repository) {
        this.logger = logger;
        this.repository = repository;
    }

    public Mono<ServerResponse> create(ServerRequest request) {

        return request.body(BodyExtractors.toMono(UpdatedPost.class)).flatMap(updatedPost -> {

            Mono<PostMappingInterface> post = repository
                    .create(new Post(updatedPost.getTitle(), updatedPost.getBody(), updatedPost.getAuthor()));

            return ServerResponse.ok().body(post, PostMappingInterface.class);
        });
    }

    public Mono<ServerResponse> read(ServerRequest request) {
        
        int id = Integer.parseInt(request.pathVariable("id"));

        return repository
            .fetchById(id)
            .onErrorReturn(Post.empty())
            .flatMap(PostHandler::handleEntityOrNotFound);
    }

    public Mono<ServerResponse> show(ServerRequest request) {
        
        Flux<PostMappingInterface> posts = repository.fetchAll();
        
        return ServerResponse.ok().body(posts, PostMappingInterface.class);
    }

    public Mono<ServerResponse> update(ServerRequest request) {
        
        return request
            .body(BodyExtractors.toMono(UpdatedPost.class))
            .flatMap(updatedPost -> repository.update(updatedPost))
            .onErrorReturn(Post.empty())
            .flatMap(PostHandler::handleEntityOrNotFound);
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        
        int id = Integer.parseInt(request.pathVariable("id"));

        return repository.delete(id)
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
