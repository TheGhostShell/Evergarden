package com.evergarden.cms.app.config.security;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * An authentication manager intended to authenticate a JWT exchange
 * JWT tokens contain all information within the token itself
 * so an authentication manager is not necessary but we provide this
 * implementation to follow a standard.
 * Invalid tokens are filtered one previous step
 */
@Component
public class EvergardenAuthenticationManager implements ReactiveAuthenticationManager {

    private JwtHelper jwtHelper;
    private Logger logger;

    @Autowired
    public EvergardenAuthenticationManager(JwtHelper jwtHelper, Logger logger) {
        this.jwtHelper = jwtHelper;
        this.logger = logger;
    }

    /**
     * Successfully authenticate an Authentication object
     *
     * @param authentication A valid authentication object
     * @return if authentication is successful an {@link Authentication} is returned.
     * If authentication cannot be determined, an empty Mono is returned.
     * If authentication fails, a Mono error is returned.
     */
    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {

        String  authToken = authentication.getCredentials().toString();
        boolean isAuth    = jwtHelper.verifyToken(authToken);

        // see https://stackoverflow.com/questions/47958622/spring-security-webflux-reactive-exception-handling
        if (!isAuth) {
            return Mono.empty();
        }

        List<SimpleGrantedAuthority> roles = jwtHelper.getRolesFromToken(authToken);
        String                       email = jwtHelper.getEmailFromToken(authToken);

        Authentication authenticated = new UsernamePasswordAuthenticationToken(email, authToken, roles);

        return Mono.just(authenticated);
    }
}
