package com.evergarden.cms.context.user.infrastructure.controller;

import com.evergarden.cms.app.utils.ExceptionUtils;
import com.evergarden.cms.context.user.application.mapper.AvatarMapper;
import com.evergarden.cms.context.user.application.service.CRUDUserService;
import com.evergarden.cms.context.user.application.service.AvatarFolderHelper;
import com.evergarden.cms.context.user.domain.exception.InvalidRoleNameException;
import com.evergarden.cms.context.user.infrastructure.controller.input.UnSaveUser;
import com.evergarden.cms.context.user.infrastructure.controller.input.UpdatedUser;
import com.evergarden.cms.context.user.infrastructure.controller.output.AvatarResponse;
import com.evergarden.cms.context.user.infrastructure.controller.output.UserResponse;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class UserHandler {

    private Logger logger;
    private CRUDUserService cRUDUserService;
    private AvatarFolderHelper avatarFolderHelper;
    @Value("#{systemProperties['user.dir']}")
    private String dir;

    @Autowired
    public UserHandler(Logger logger, CRUDUserService cRUDUserService, AvatarFolderHelper avatarFolderHelper) {

        this.logger = logger;
        this.cRUDUserService = cRUDUserService;
        this.avatarFolderHelper = avatarFolderHelper;
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        Mono<UnSaveUser> unSaveUserMono = request.body(BodyExtractors.toMono(UnSaveUser.class));
        return cRUDUserService.createUser(unSaveUserMono)
            .flatMap(userResponse -> ServerResponse.ok().body(BodyInserters.fromObject(userResponse)))
            .onErrorResume(throwable -> {
                logger.warn(throwable.toString());
                String err = ExceptionUtils.getRootCause(throwable).getClass() == InvalidRoleNameException.class
                    ? ExceptionUtils.getRootCause(throwable).getMessage() : "Error in payload";
                return ServerResponse.badRequest().body(Mono.just(err), String.class);
            });
    }

    public Mono<ServerResponse> read(ServerRequest request) {
        return cRUDUserService.readUser(request.pathVariable("id"))
            .flatMap(userResponse -> ServerResponse.ok().body(Mono.just(userResponse), UserResponse.class))
            .onErrorResume(throwable -> {
                logger.warn(throwable.toString());
                return ServerResponse.notFound().build();
            });
    }

    // TODO refactor and use private method as create()
    // TODO use id from token for more security is the right behavior
    Mono<ServerResponse> update(ServerRequest request) {
        Mono<UpdatedUser> updatedUserMono = request.body(BodyExtractors.toMono(UpdatedUser.class));
        return cRUDUserService.updateUser(updatedUserMono)
            .flatMap(userResponse -> ServerResponse.ok().body(Mono.just(userResponse), UserResponse.class))
            .onErrorResume(throwable -> ServerResponse.badRequest()
                .body(Mono.just(ExceptionUtils.getRootCause(throwable).getMessage()), String.class));
    }

    // TODO implement
    Mono<ServerResponse> updatePassword(ServerRequest serverRequest) {
        return ServerResponse.ok().build();
    }

    public Mono<ServerResponse> show(ServerRequest request) {
        return ServerResponse.ok()
            .body(cRUDUserService.showUser(), UserResponse.class);
    }

    public Mono<ServerResponse> updateAvatar(ServerRequest serverRequest) {
        return serverRequest
            .body(BodyExtractors.toMultipartData())
            .map(mvPart -> {
                avatarFolderHelper.createFolder(dir + "/images/avatar/");
                return mvPart;
            })
            .flatMap(part -> {
                String token = serverRequest.headers().header("Authorization").get(0);
                return avatarFolderHelper.processAvatarFile(part, dir + "/images/avatar/", token)
                    .then(ServerResponse.ok().build());
            });
    }

    public Mono<ServerResponse> readAvatar(ServerRequest serverRequest) {
        return cRUDUserService.readUser(serverRequest.pathVariable("userId"))
            .flatMap(userResponse -> ServerResponse.ok().body(
                Mono.just(AvatarMapper.INSTANCE.userResponseToAvatarResponse(userResponse)),
                AvatarResponse.class
            ))
            .onErrorResume(throwable -> {
                logger.warn(throwable.toString());
                return ServerResponse.notFound().build();
            });
    }
}
