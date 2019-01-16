package com.evergarden.cms.context.admin.application.bearer;

import com.evergarden.cms.context.admin.application.jwt.JWTCustomVerifier;
import com.evergarden.cms.context.admin.domain.entity.RoleEnum;
import com.nimbusds.jwt.SignedJWT;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
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
    
    Logger logger;
    
    @Autowired
    public EvergardenAuthenticationManager(Logger logger) {
        this.logger = logger;
    }
    
    /**
     * Successfully authenticate an Authentication object
     *
     * @param authentication A valid authentication object
     * @return authentication A valid authentication object
     */
    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        
        String            authToken         = authentication.getCredentials().toString();
        JWTCustomVerifier jwtCustomVerifier = new JWTCustomVerifier();
        
        Mono<SignedJWT> jwtCustomSignerMono = jwtCustomVerifier.check(authToken);

        List<SimpleGrantedAuthority> roles = new ArrayList<>();

        roles.add(new SimpleGrantedAuthority(RoleEnum.MASTER_ADMIN.toString()));
        roles.add(new SimpleGrantedAuthority(RoleEnum.GUEST.toString()));
        roles.add(new SimpleGrantedAuthority(RoleEnum.ADMIN.toString()));
        roles.add(new SimpleGrantedAuthority(RoleEnum.USER.toString()));

        Authentication au = new UsernamePasswordAuthenticationToken("violet",null, roles);
        
        return Mono.just(au);
    }
}
