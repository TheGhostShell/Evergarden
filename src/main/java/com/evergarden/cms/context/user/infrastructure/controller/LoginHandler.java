package com.evergarden.cms.context.user.infrastructure.controller;

import com.evergarden.cms.context.user.application.mapper.LoginMapper;
import com.evergarden.cms.context.user.application.service.GenerateGuestTokenService;
import com.evergarden.cms.context.user.application.service.GenerateTokenService;
import com.evergarden.cms.context.user.domain.entity.Guest;
import com.evergarden.cms.context.user.domain.entity.Token;
import com.evergarden.cms.context.user.infrastructure.controller.input.Logout;
import com.evergarden.cms.context.user.infrastructure.controller.input.UnAuthUser;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class LoginHandler {

  private GenerateTokenService generateTokenService;
  private GenerateGuestTokenService generateGuestTokenService;

  @Autowired
  public LoginHandler(
      GenerateTokenService generateTokenService,
      GenerateGuestTokenService generateGuestTokenService) {
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
    return request
        .body(BodyExtractors.toMono(Guest.class))
        .map(guest -> generateGuestTokenService.generateGuestToken(guest))
        .flatMap(guestMono -> ServerResponse.ok().body(guestMono, Guest.class))
        .onErrorResume(throwable -> ServerResponse.badRequest().build());
  }

  Mono<ServerResponse> login(ServerRequest request) {
    log.debug("Starting generate token");
    return request
        .body(BodyExtractors.toMono(UnAuthUser.class))
        .flatMap(unAuthUser -> generateTokenService.generateToken(unAuthUser))
        .map(token -> Mono.just(LoginMapper.INSTANCE.tokenToLoginResponse(token)))
        .flatMap(loginResponseMono -> ServerResponse.ok().body(loginResponseMono, Token.class))
        .onErrorResume(
            throwable -> {
              log.warn(throwable.toString());
              return ServerResponse.badRequest().build();
            });
  }

  public Mono<ServerResponse> admin(ServerRequest request) {
    Resource html = new ClassPathResource("/public/admin/index.html");
    return ServerResponse.ok().contentType(MediaType.TEXT_HTML).bodyValue(html);
  }

  Mono<ServerResponse> home(ServerRequest request) {
    return ServerResponse.ok()
        .contentType(MediaType.TEXT_HTML)
        .bodyValue(new FileSystemResource("./template/theme/index.html"));
  }

  public Mono<ServerResponse> logout(ServerRequest serverRequest) {
    return serverRequest
        .body(BodyExtractors.toMono(Logout.class))
        .flatMap(logout -> Mono.just(generateTokenService.removeTokenFromCache(logout.getId())))
        .flatMap(aBoolean -> ServerResponse.ok().build());
  }
}
