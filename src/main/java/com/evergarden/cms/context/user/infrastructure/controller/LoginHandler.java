package com.evergarden.cms.context.user.infrastructure.controller;

import com.evergarden.cms.context.user.application.service.GenerateGuestTokenService;
import com.evergarden.cms.context.user.application.service.GenerateTokenService;
import com.evergarden.cms.context.user.domain.entity.Guest;
import com.evergarden.cms.context.user.domain.entity.Token;
import com.evergarden.cms.context.user.infrastructure.controller.input.Logout;
import com.evergarden.cms.context.user.infrastructure.controller.input.UnAuthUser;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class LoginHandler {

    private Logger logger;
    private GenerateTokenService generateTokenService;
    private GenerateGuestTokenService generateGuestTokenService;

    @Autowired
    public LoginHandler(Logger logger,
                        GenerateTokenService generateTokenService,
                        GenerateGuestTokenService generateGuestTokenService) {

        this.logger = logger;
        this.generateTokenService = generateTokenService;
        this.generateGuestTokenService = generateGuestTokenService;
    }

    /**
     * Some resource it exposed for visitor and we need to know you want visit without authenticate so
     * this resource will create a token for anonymous visitor with 5 minutes expiration date
     *
     * @param request
     * @return
     */
    public Mono<ServerResponse> guest(ServerRequest request) {
        Mono<Guest> guestMono = generateGuestTokenService
            .generateGuestToken(request.body(BodyExtractors.toMono(Guest.class)));

        return ServerResponse.ok().body(guestMono, Guest.class)
            .onErrorResume(throwable -> ServerResponse.badRequest().build());
    }

    Mono<ServerResponse> login(ServerRequest request) {
        Mono<UnAuthUser> unAuthUserMono = request.body(BodyExtractors.toMono(UnAuthUser.class));
        Mono<Token> tokenMono = this.generateTokenService.generateToken(unAuthUserMono);
        return tokenMono.flatMap(token -> ServerResponse.ok().body(BodyInserters.fromValue(token)))
            .onErrorResume(throwable -> {
                logger.warn(throwable.toString());
                return ServerResponse.badRequest().build();
            });
    }

    public Mono<ServerResponse> admin(ServerRequest request) {
        Resource html = new ClassPathResource("/public/admin/index.html");
        return ServerResponse.ok()
            .contentType(MediaType.TEXT_HTML).bodyValue(html);
    }

    Mono<ServerResponse> home(ServerRequest request) {
        return ServerResponse.ok()
            .contentType(MediaType.TEXT_HTML).bodyValue(new FileSystemResource("./template/theme/index.html"));
    }

    public Mono<ServerResponse> logout(ServerRequest serverRequest) {
        return serverRequest.body(BodyExtractors.toMono(Logout.class))
            .flatMap(logout -> Mono.just(generateTokenService.removeTokenFromCache(logout.getId())))
            .flatMap(aBoolean -> ServerResponse.ok().build());
    }
}
