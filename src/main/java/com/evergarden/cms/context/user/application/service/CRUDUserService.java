package com.evergarden.cms.context.user.application.service;

import com.evergarden.cms.app.config.security.EvergardenEncoder;
import com.evergarden.cms.app.utils.ExceptionUtils;
import com.evergarden.cms.context.user.application.mapper.UpdateUserMapper;
import com.evergarden.cms.context.user.application.mapper.UserMapper;
import com.evergarden.cms.context.user.domain.entity.Profile;
import com.evergarden.cms.context.user.domain.entity.User;
import com.evergarden.cms.context.user.domain.exception.RessourceNotFoundException;
import com.evergarden.cms.context.user.infrastructure.controller.input.UnSaveUser;
import com.evergarden.cms.context.user.infrastructure.controller.input.UpdatedUser;
import com.evergarden.cms.context.user.infrastructure.controller.output.UserResponse;
import com.evergarden.cms.context.user.infrastructure.persistence.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Service
@Slf4j
public class CRUDUserService {
    private UserRepository     userRepository;
    private CRUDProfileService crudProfileService;
    private EvergardenEncoder  encoder;

    @Autowired
    public CRUDUserService(UserRepository userRepository, CRUDProfileService crudProfileService,
                           EvergardenEncoder encoder) {
        this.userRepository = userRepository;
        this.crudProfileService = crudProfileService;
        this.encoder = encoder;
    }

    public Mono<UserResponse> createUser(@Valid UnSaveUser unSaveUser) {
        return crudProfileService.findByNameOrId(unSaveUser.getProfile()
            .getName(), unSaveUser.getProfile()
            .getId())
            .map(profile -> {
                User user = UserMapper.INSTANCE.unSaveUserToUser(unSaveUser); // TODO need to be deeply tested
                user.setProfile(profile);
                encoder.encode(unSaveUser.getPassword());
                user.setEncodedCredential(encoder.getEncodedCredential());
                return user;
            })
            .flatMap(user -> {
                log.debug("Try to create user " + user.getEmail());
                return userRepository.save(user);
            })
            .flatMap(user -> {
                log.debug("New user saved with id " + user.getId());
                return Mono.just(UserMapper.INSTANCE.userToUserResponse(user));
            })
            .doOnError(throwable -> log.warn(throwable.toString()));
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

    // TODO deep test needed
    public Mono<UserResponse> updateUser(UpdatedUser updatedUser) {
        return Mono.just(updatedUser)
            .flatMap(upUser -> userRepository.findById(upUser.getId())
                .flatMap(user -> Mono.just(UpdateUserMapper.toUser(upUser, user)))
                .flatMap(user -> crudProfileService.findByNameOrId(user.getProfile()
                    .getName(), user.getProfile()
                    .getId())
                    .map(profile -> {
                        user.setProfile(profile);
                        return user;
                    }))
                .switchIfEmpty(Mono.error(new RessourceNotFoundException(upUser.getId()))))
            .flatMap(user -> userRepository.save(user))
            .flatMap(user -> Mono.just(UserMapper.INSTANCE.userToUserResponse(user)))
            .onErrorResume(throwable -> {
                log.warn(ExceptionUtils.getRootCause(throwable)
                    .getMessage());
                return Mono.error(throwable);
            });
    }

    public Mono<User> updateOrSave(User user) {
        return userRepository.save(user);
    }

    public Mono<User> findFirstByProfile(Profile profile) {
        return userRepository.findFirstByProfile(profile)
            .switchIfEmpty(Mono.error(new RessourceNotFoundException(" for User with profile " + profile.getName())));
    }
}
