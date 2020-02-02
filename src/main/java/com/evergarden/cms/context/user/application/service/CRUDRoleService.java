package com.evergarden.cms.context.user.application.service;

import com.evergarden.cms.context.user.domain.entity.Role;
import com.evergarden.cms.context.user.domain.entity.User;
import com.evergarden.cms.context.user.infrastructure.persistence.RoleRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class CRUDRoleService {

    private RoleRepository roleRepository;
    private Logger logger;

    @Autowired
    public CRUDRoleService(RoleRepository roleRepository, Logger logger) {
        this.roleRepository = roleRepository;
        this.logger = logger;
    }

    public Mono<User> assignRoleToUser(User user){
        logger.debug("Try to assign role to user " + user.getEmail());
        Collection<Role> roles = new ArrayList<>(user.getRoles());
        user.clearRole();

        logger.debug("Role for user " + user.getEmail() +" is cleared ");
        roles.forEach(role -> logger.debug("Role to assign "+role.toString()));

        return Flux.fromIterable(roles)
            .flatMap(role -> roleRepository.findByRole(role.getRole())
                .map(role1 -> {
                    logger.debug("Founded role "+ role1.getRole() +" id "+role1.getId());
                    user.addRole(role1);
                    return user;
                })
                .switchIfEmpty(Mono.defer(() -> roleRepository.save(role)
                    .map(role1 -> {
                        logger.debug("Created role "+ role1.getRole() +" id "+role1.getId());
                        user.addRole(role1);
                        return user;
                    })
                    .then(Mono.just(user))
                )
            ))
            .then(Mono.just(user));
    }

    public Flux<Role> findAllRole(){
        return roleRepository.findAll();
    }
}
