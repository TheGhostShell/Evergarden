package com.evergarden.cms.context.user.infrastructure.persistence;

import com.evergarden.cms.context.user.domain.entity.Role;
import com.evergarden.cms.context.user.domain.entity.User;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, String> {

    Mono<User> findByEmail(String email);

    Mono<User> findFirstByRoles(Role role);

    @Query(value = "{'roles.role': ?0}", fields = "{'roles.role': 0}")
    Mono<User> findFirstByRolesRole(String role);
}
