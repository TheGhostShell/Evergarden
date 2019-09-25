package com.evergarden.cms.context.admin.infrastructure.persistence;

import com.evergarden.cms.context.admin.domain.entity.Role;
import com.evergarden.cms.context.admin.domain.entity.UpdatedUser;
import com.evergarden.cms.context.admin.domain.entity.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, String> {

    Mono<User> findByEmail(String email);

    Flux<User> fetchAll();

    Mono<Role> createUserRole(Role role, int userId);

    Mono<Role> createRole(Role role);

    Mono<Role> findRole(Role role);

    Mono<Role> findRoleByCriteria(Role role, int userId);

    Mono<Integer> create(User user);

    Mono<User> findFirstByRole(Role role);

    Mono<User> update(UpdatedUser user);
}
