package com.evergarden.cms.context.publisher.infrastructure.controller;

import com.evergarden.cms.app.config.security.JwtHelper;
import com.evergarden.cms.context.publisher.application.mapper.PostMapper;
import com.evergarden.cms.context.publisher.application.service.CRUDPostService;
import com.evergarden.cms.context.publisher.application.utils.FastCacheUser;
import com.evergarden.cms.context.publisher.domain.entity.Post;
import com.evergarden.cms.context.publisher.infrastructure.controller.input.UnSavePostRequest;
import com.evergarden.cms.context.publisher.infrastructure.controller.output.PostResponse;
import com.evergarden.cms.context.publisher.infrastructure.controller.output.PostSummaryResponse;
import com.evergarden.cms.context.user.domain.entity.TokenDecrypted;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
@Slf4j
public class PostHandler {

    private final CRUDPostService crudPostService;
    private final JwtHelper jwtHelper;
    private final FastCacheUser fastCacheUser;
    private final PostMapper postMapper = PostMapper.INSTANCE;

    @Autowired
    public PostHandler(CRUDPostService crudPostService, JwtHelper jwtHelper,
                       FastCacheUser fastCacheUser) {
        this.crudPostService = crudPostService;
        this.jwtHelper = jwtHelper;
        this.fastCacheUser = fastCacheUser;
    }

    public Mono<ServerResponse> create(ServerRequest request) {

        TokenDecrypted token = jwtHelper.fromServerRequest(request);

        return request.body(BodyExtractors.toMono(UnSavePostRequest.class))
            .flatMap(unSavedPost -> {
                Post postToSave = postMapper.unSavePostToPost(unSavedPost);

                postToSave.setAuthorId(token.getUserId());
                postToSave.setCreatedAt(LocalDateTime.now());

                Mono<Post> postMono = crudPostService.create(postToSave);

                // Todo missing tags
                Mono<PostSummaryResponse> postSummaryMono = postMono.flatMap(
                    post -> postMapper.toPostSummary(post, fastCacheUser));

                return ServerResponse.ok()
                    .body(postSummaryMono, PostSummaryResponse.class);
            });
    }

    public Mono<ServerResponse> read(ServerRequest request) {

        String id = request.pathVariable("id");

        return crudPostService.findById(id)
            .flatMap(post -> ServerResponse.ok()
                .body(postMapper.toPostWithBody(post, fastCacheUser), PostResponse.class))
            .onErrorResume(throwable -> ServerResponse.badRequest()
                .body(BodyInserters.fromValue(throwable.getMessage())));
    }

    public Mono<ServerResponse> show(ServerRequest request) {

        Flux<PostSummaryResponse> summaryResponseFlux = crudPostService.findAll()
            .flatMap(post -> postMapper.toPostSummary(post, fastCacheUser));

        return ServerResponse.ok()
            .body(summaryResponseFlux, PostSummaryResponse.class)
            .onErrorResume(throwable -> ServerResponse.noContent()
                .build());
    }

    public Mono<ServerResponse> update(ServerRequest request) {

        return request.body(BodyExtractors.toMono(Post.class))
            .flatMap(crudPostService::updatePost)
            .flatMap(post -> ServerResponse.ok()
                .body(BodyInserters.fromValue(post)))
            .onErrorResume(throwable -> ServerResponse.badRequest()
                .body(BodyInserters.fromValue(throwable.getMessage())));
    }

    public Mono<ServerResponse> delete(ServerRequest request) {

        String id = request.pathVariable("id");

        return crudPostService.deleteById(id)
            .flatMap(deletedPost -> ServerResponse.ok()
                .build());
    }
}
