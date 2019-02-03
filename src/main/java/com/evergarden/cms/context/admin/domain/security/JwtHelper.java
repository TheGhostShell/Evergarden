package com.evergarden.cms.context.admin.domain.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.evergarden.cms.context.admin.domain.entity.Token;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class JwtHelper {
    // TODO It's better to declare a bean to create a singleton instance of JwtHelper to inject as dependency in class to avoid using static method and call gc

    private JwtRequest jwtRequest;

    private Algorithm   algorithm;
    private Logger      logger;
    private JWTVerifier verifier;

    private static final String ISSUER = "evergarden";

    @Autowired
    public JwtHelper(JwtRequest jwtRequest, Logger logger) {
        this.jwtRequest = jwtRequest;
        this.algorithm = Algorithm.HMAC256(jwtRequest.getJwtSecret());
        this.logger = logger;
        this.verifier = JWT.require(algorithm).withIssuer(JwtHelper.ISSUER).build();

    }

    public Token generateToken(String email, ArrayList<SimpleGrantedAuthority> authorities) {
        return generateToken(email, authorities, 6L);
    }

    public Token generateToken(String email, ArrayList<SimpleGrantedAuthority> authorities, Long hours) {
        String[] roles = new String[authorities.size()];

        for (int i = 0; i < authorities.size(); i++) {
            roles[i] = authorities.get(i).getAuthority();
        }

        String token = JWT.create()
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

    public List<SimpleGrantedAuthority> getRolesFromToken(String token) {

        DecodedJWT jwt   = verifier.verify(token);
        Claim      role  = jwt.getClaim("role");
        String[]   roles = role.asArray(String.class);

        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();

        for (String item : roles) {
            authorityList.add(new SimpleGrantedAuthority(item));
        }
        return authorityList;
    }

    public String getEmailFromToken(String token) {

        DecodedJWT jwt   = verifier.verify(token);
        Claim      email = jwt.getClaim("email");

        return email.as(String.class);
    }
}
