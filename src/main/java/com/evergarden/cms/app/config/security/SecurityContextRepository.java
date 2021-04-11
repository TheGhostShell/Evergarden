package com.evergarden.cms.app.config.security;

import com.evergarden.cms.context.user.domain.entity.Token;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class SecurityContextRepository implements ServerSecurityContextRepository {

  private EvergardenAuthenticationManager authenticationManager;
  private JwtHelper jwtHelper;

  @Autowired
  public SecurityContextRepository(
      EvergardenAuthenticationManager authenticationManager, JwtHelper jwtHelper) {
    this.authenticationManager = authenticationManager;
    this.jwtHelper = jwtHelper;
  }

  @Override
  public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
    throw new UnsupportedOperationException("Not supported yet!");
  }

  @Override
  public Mono<SecurityContext> load(ServerWebExchange exchange) {

    ServerHttpRequest request = exchange.getRequest();
    String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
    Token token = jwtHelper.sanitizeHeaderToken(authHeader);

    if (authHeader != null && jwtHelper.isStartWithBearer(authHeader) && jwtHelper.verifyTokenCache(token.getTokenString())) {

      log.debug("load token from request header");
      Authentication auth = new UsernamePasswordAuthenticationToken(token.getEmail(), token.getTokenString());

      return this.authenticationManager.authenticate(auth).map(SecurityContextImpl::new);

    } else {
      log.debug("Request doesn't contain authorization header");
      return Mono.empty();
    }
  }
}
