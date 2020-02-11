package com.evergarden.cms.context.user.application.service;

import com.evergarden.cms.app.config.security.EvergardenEncoder;
import com.evergarden.cms.app.utils.ExceptionUtils;
import com.evergarden.cms.context.user.application.mapper.UpdateUserMapper;
import com.evergarden.cms.context.user.application.mapper.UserMapper;
import com.evergarden.cms.context.user.domain.entity.User;
import com.evergarden.cms.context.user.domain.exception.RessourceNotFoundException;
import com.evergarden.cms.context.user.infrastructure.controller.input.UnSaveUser;
import com.evergarden.cms.context.user.infrastructure.controller.input.UpdatedUser;
import com.evergarden.cms.context.user.infrastructure.controller.output.UserResponse;
import com.evergarden.cms.context.user.infrastructure.persistence.UserRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CRUDUserService {
    private UserRepository  userRepository;
    private CRUDRoleService assignRoleUserService;
    private Logger logger;
    private EvergardenEncoder encoder;

    @Autowired
    public CRUDUserService(UserRepository userRepository,
                           CRUDRoleService assignRoleUserService,
                           Logger logger,
                           EvergardenEncoder encoder) {
        this.userRepository = userRepository;
        this.assignRoleUserService = assignRoleUserService;
        this.logger = logger;
        this.encoder = encoder;
    }

    public Mono<UserResponse> createUser(UnSaveUser unSaveUser) {
        return Mono.just(unSaveUser)
            .flatMap(unSaveUser1 -> {
                User user = UserMapper.INSTANCE.unSaveUserToUser(unSaveUser1);
                encoder.encode(unSaveUser1.getPassword());
                user.setEncodedCredential(encoder.getEncodedCredential());
                return assignRoleUserService.assignRoleToUser(user);
            })
            .flatMap(user -> {
                logger.debug("Try to create user " + user.getEmail());
                return userRepository.save(user);
            })
            .flatMap(user -> {
                logger.debug("New user saved with id " + user.getId());
                return Mono.just(UserMapper.INSTANCE.userToUserResponse(user));
            })
            .doOnError(throwable -> logger.warn(throwable.toString()));
    }

    public Mono<UserResponse> readUser(String id) {
        return userRepository.findById(id)
            .flatMap(user -> {
                UserResponse us = UserResponse.mapToUserResponse(user);
                return Mono.just(us);
            })
            .switchIfEmpty(Mono.error(new RessourceNotFoundException(id)));
    }

    public Mono<User> findUser(String id) {
        return userRepository.findById(id);
    }

    public Flux<UserResponse> showUser() {
        return userRepository.findAll()
            .map(UserResponse::mapToUserResponse)
            .switchIfEmpty(Mono.error(new RessourceNotFoundException("User")));
    }

    public Mono<UserResponse> updateUser(UpdatedUser updatedUser) {
        return Mono.just(updatedUser)
            .flatMap(upUser -> userRepository.findById(upUser.getId())
                .flatMap(user -> Mono.just(UpdateUserMapper.toUser(upUser, user)))
                .switchIfEmpty(Mono.error(new RessourceNotFoundException(upUser.getId())))
            )
            .flatMap(user -> assignRoleUserService.assignRoleToUser(user))
            .flatMap(user -> userRepository.save(user))
            .flatMap(user -> Mono.just(UserMapper.INSTANCE.userToUserResponse(user)))
            .onErrorResume(throwable -> {
                logger.warn(ExceptionUtils.getRootCause(throwable).getMessage());
                return Mono.error(throwable);
            });
    }

    public Mono<User> update(User user) {
        return userRepository.save(user);
    }
}
