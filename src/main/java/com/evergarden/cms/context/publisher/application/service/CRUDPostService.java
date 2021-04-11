package com.evergarden.cms.context.publisher.application.service;

import com.evergarden.cms.context.publisher.domain.entity.Post;
import com.evergarden.cms.context.publisher.domain.exception.RessourceNotFoundException;
import com.evergarden.cms.context.publisher.infrastructure.persistence.PostRepository;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class CRUDPostService {
    private PostRepository postRepository;

    @Autowired
    public CRUDPostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Mono<Post> create(Post post){
        return postRepository.save(post);
    }

    public Mono<Post> findById(String id) {
        return postRepository.findById(id)
            .switchIfEmpty(Mono.error(new RessourceNotFoundException(id)));
    }

    public Flux<Post> findAll(){
        return postRepository.findAll();
    }

    public Mono<Post> updatePost(@NonNull Post post){
        // TODO its really updated with new authorId and date field UpdatedAt
        return postRepository.findById(post.getId())
            .switchIfEmpty(Mono.error(new RessourceNotFoundException(post.getId())))
            .flatMap(unchangedPost -> postRepository.save(post));
    }

    public Mono<Void> deleteById(String id) {
        return postRepository.deleteById(id);
    }
}
