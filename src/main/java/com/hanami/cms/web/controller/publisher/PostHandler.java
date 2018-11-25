package com.hanami.cms.web.controller.publisher;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class PostHandler {

    private Logger logger;

    @Autowired
    public PostHandler(Logger logger) {
        this.logger = logger;
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        logger.info("Request for creating post handle well");
        return ServerResponse.ok().body(BodyInserters.fromObject(request.path()));
    }
}
