package com.evergarden.cms.context.user.infrastructure.persistence;

import com.evergarden.cms.context.user.domain.entity.Role;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface RoleRepository extends ReactiveMongoRepository<Role, String> {

    Mono<Role> findByRole(String role);
}
