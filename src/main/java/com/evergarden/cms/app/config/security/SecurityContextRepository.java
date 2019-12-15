package com.evergarden.cms.app.config.security;

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
public class SecurityContextRepository implements ServerSecurityContextRepository {

    private EvergardenAuthenticationManager authenticationManager;
    private JwtHelper jwtHelper;

    @Autowired
    public SecurityContextRepository(EvergardenAuthenticationManager authenticationManager, JwtHelper jwtHelper) {
        this.authenticationManager = authenticationManager;
        this.jwtHelper = jwtHelper;
    }

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        throw new UnsupportedOperationException("Not supported yet!");
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {

        ServerHttpRequest request    = exchange.getRequest();
        String            authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer ") && jwtHelper.verifyTokenCache(authHeader.substring(7)) ) {

            String         authToken = authHeader.substring(7);
            Authentication auth      = new UsernamePasswordAuthenticationToken(authToken, authToken);

            return this.authenticationManager
                .authenticate(auth)
                .map(SecurityContextImpl::new);

        } else {
            return Mono.empty();
        }
    }
}
