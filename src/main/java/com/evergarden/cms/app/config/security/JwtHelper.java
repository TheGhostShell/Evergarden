package com.evergarden.cms.app.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.evergarden.cms.context.user.domain.entity.Token;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.cache.Cache;
import java.sql.Date;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class JwtHelper {
    // TODO It's better to declare a bean to create a singleton instance of JwtHelper to inject as dependency in class to avoid using static method and call gc

    private JwtRequest jwtRequest;

    private Algorithm   algorithm;
    private Logger      logger;
    private JWTVerifier verifier;
    private Cache<String, Token> tokenCache;

    private static final String ISSUER = "evergarden";

    @Autowired
    public JwtHelper(JwtRequest jwtRequest, Logger logger, Cache<String, Token> tokenCache) {
        this.jwtRequest = jwtRequest;
        this.algorithm = Algorithm.HMAC256(jwtRequest.getJwtSecret());
        this.logger = logger;
        this.tokenCache = tokenCache;
        this.verifier = JWT.require(algorithm).withIssuer(JwtHelper.ISSUER).build();

    }

    @Cacheable(value = "tokenCache", key = "#id")
    public Token generateToken(String email, ArrayList<SimpleGrantedAuthority> authorities, String id) {
        return generateToken(email, authorities, 6L, id);
    }

    public Token generateToken(String email, ArrayList<SimpleGrantedAuthority> authorities, Long hours, String id) {
        String[] roles = new String[authorities.size()];

        for (int i = 0; i < authorities.size(); i++) {
            roles[i] = authorities.get(i).getAuthority();
        }

        String token = JWT.create()
            .withClaim("id", id)
            .withClaim("email", email)
            .withArrayClaim("role", roles)
            .withIssuer(JwtHelper.ISSUER)
            .withExpiresAt(Date.from(Instant.now().plus(Duration.ofHours(hours))))
            .sign(algorithm);

        return new Token(token);
    }

    public boolean verifyToken(String token) {
        try {
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    public List<SimpleGrantedAuthority> getRolesFromToken(String token) throws JWTVerificationException {

        DecodedJWT jwt   = verifier.verify(token);
        Claim      role  = jwt.getClaim("role");
        String[]   roles = role.asArray(String.class);

        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();

        for (String item : roles) {
            authorityList.add(new SimpleGrantedAuthority(item));
        }
        return authorityList;
    }

    public String getEmailFromToken(String token) throws JWTVerificationException {

        DecodedJWT jwt   = verifier.verify(token);
        Claim      email = jwt.getClaim("email");

        return email.as(String.class);
    }

    public String getIdFromToken(String token) throws JWTVerificationException {

        DecodedJWT jwt   = verifier.verify(token);
        Claim      id = jwt.getClaim("id");

        return id.as(String.class);
    }

    public boolean verifyTokenCache(String token) {
        try {
            String id = this.getIdFromToken(token);
            return Optional.ofNullable(tokenCache.get(id))
                .flatMap(token1 -> {
                    logger.debug("Load token from cache for user with id {}", id);
                    return Optional.of(token.equals(token1.getToken()));
                })
                .orElse(false);

        }catch (JWTVerificationException e) {
            return false;
        }
    }
}
