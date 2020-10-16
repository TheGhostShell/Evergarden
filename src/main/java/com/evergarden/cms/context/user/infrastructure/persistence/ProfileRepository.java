package com.evergarden.cms.context.user.infrastructure.persistence;

import com.evergarden.cms.context.user.domain.entity.Profile;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ProfileRepository extends ReactiveMongoRepository<Profile, String> {

    Mono<Profile> findFirstByName(String name);
    Mono<Profile> findById(String id);
}
