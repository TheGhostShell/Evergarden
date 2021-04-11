package com.evergarden.cms.context.publisher.application.mapper;

import com.evergarden.cms.context.publisher.application.utils.FastCacheUser;
import com.evergarden.cms.context.publisher.domain.entity.Post;
import com.evergarden.cms.context.publisher.infrastructure.controller.input.UnSavePostRequest;
import com.evergarden.cms.context.publisher.infrastructure.controller.output.PostResponse;
import com.evergarden.cms.context.publisher.infrastructure.controller.output.PostSummaryResponse;
import com.evergarden.cms.context.user.domain.entity.EncodedCredential;
import com.evergarden.cms.context.user.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

@Mapper
public interface PostMapper {
    PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);

    Post unSavePostToPost(UnSavePostRequest unSavePost);

    default Mono<PostSummaryResponse> toPostSummary(Post post, FastCacheUser fastCacheUser) {
        Mono<User> userMono = postToUser.apply(post,
            fastCacheUser); // TODO all post should have an author !!

        return userMono.map(userData -> PostSummaryResponse.builder()
            .id(post.getId())
            .authorId(post.getAuthorId())
            .summary(post.getSummary())
            .authorFullName(userData.getFirstname()
                .concat(" ")
                .concat(userData.getLastname()))
            .title(post.getTitle())
            .build());
    }

    Supplier<User> defaultUser = () -> User.builder()
        .firstname("")
        .lastname("")
        .encodedCredential(new EncodedCredential("", ""))
        .build();

    BiFunction<Post, FastCacheUser, Mono<User>> postToUser = (Post post, FastCacheUser fastCacheUser) -> Optional.ofNullable(
        post.getAuthorId())
        .map(fastCacheUser::findUserById)
        .orElse(Mono.just(defaultUser.get()));

    default Mono<PostResponse> toPostWithBody(Post post,
                                              FastCacheUser fastCacheUser) { // TODO use specific mapper
        Mono<User> userMono = postToUser.apply(post, fastCacheUser);

        return userMono.map(userData -> PostResponse.builder()
            .id(post.getId())
            .authorId(post.getAuthorId())
            .summary(post.getSummary())
            .body(post.getBody())
            .authorFullName(userData.getFirstname()
                .concat(" ")
                .concat(userData.getLastname()))
            .title(post.getTitle())
            .build());
    }
}
