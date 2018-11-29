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

        Flux<Post> posts = repository.fetchAll();

        return ServerResponse.ok().body(posts, Post.class);
    }

    public Mono<ServerResponse> read(ServerRequest request) {

        int id = Integer.parseInt(request.pathVariable("id"));

        return repository.fetchById(id)
                .onErrorReturn(Post.empty())
                .flatMap(PostHandler::handleEntityOrNotFound);

    }

    private static Mono<ServerResponse> handleEntityOrNotFound(com.hanami.cms.entity.publisher.mapping.Post post) {
        if (post.getId() != 0) {
            return ServerResponse.ok().body(BodyInserters.fromObject(post));
        } else {
            return ServerResponse.notFound().build();
        }
    }
}
