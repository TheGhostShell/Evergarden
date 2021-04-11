package com.evergarden.cms.context.user.application.service;

import com.evergarden.cms.context.user.domain.entity.Role;
import com.evergarden.cms.context.user.domain.exception.RessourceNotFoundException;
import com.evergarden.cms.context.user.infrastructure.persistence.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CRUDRoleService {

    private RoleRepository roleRepository;

    @Autowired
    public CRUDRoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Flux<Role> findAllRole(){
        return roleRepository.findAll();
    }

    public Mono<Role> findRoleByName(String name) {
        return roleRepository.findByRole(name)
            .doOnNext(role -> log.debug("find by name this " + role.getRole()))
            .switchIfEmpty(Mono.error(new RessourceNotFoundException(name)));
    }

    public Mono<List<Role>> saveRoles(List<Role> roles){
        return Flux.fromIterable(roles)
            .flatMap(role ->
                this.findRoleByName(role.getRole())
                    .onErrorResume(throwable -> this.save(role)))
            .collect(Collectors.toList());
    }

    public Mono<List<Role>> findByRoles(List<String> roles) {
        return Flux.fromIterable(roles)
            .flatMap(this::findRoleByName)
            .collectList();
    }

    public Mono<Role> save(Role role) {
        return roleRepository.save(role);
    }

    public Mono<Role> findById(String id) {
        return roleRepository.findById(id);
    }
}
