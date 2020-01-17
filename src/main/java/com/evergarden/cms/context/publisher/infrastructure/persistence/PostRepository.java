package com.evergarden.cms.context.publisher.infrastructure.persistence;

import com.evergarden.cms.context.publisher.domain.entity.Post;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends ReactiveMongoRepository<Post, String> {
}
