package com.evergarden.cms.app.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.evergarden.cms.context.user.domain.entity.Profile;
import com.evergarden.cms.context.user.domain.entity.Token;
import com.evergarden.cms.context.user.domain.entity.TokenDecrypted;
import com.evergarden.cms.context.user.domain.exception.InvalidTokenFormatException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import javax.cache.Cache;
import java.sql.Date;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class JwtHelper {

  private Algorithm algorithm;
  private JWTVerifier verifier;
  private Cache<String, Token> tokenCache;

  private static final String ISSUER = "evergarden";

  @Autowired
  public JwtHelper(JwtRequest jwtRequest, Cache<String, Token> tokenCache) {
    this.algorithm = Algorithm.HMAC256(jwtRequest.getJwtSecret());
    this.tokenCache = tokenCache;
    this.verifier = JWT.require(algorithm).withIssuer(JwtHelper.ISSUER).build();
  }

  // TODO use set instead of list to avoid duplicate role
  @Cacheable(value = "tokenCache", key = "#id")
  public Token generateToken(
      String email, ArrayList<SimpleGrantedAuthority> authorities, String id, Profile profile) {
    return generateToken(email, authorities, 6L, id, profile);
  }

  @Cacheable(value = "tokenCache", key = "#id")
  public Token generateToken(
      String email,
      ArrayList<SimpleGrantedAuthority> authorities,
      Long hours,
      String id,
      Profile profile) {
    String[] roles = new String[authorities.size()];

    for (int i = 0; i < authorities.size(); i++) {
      roles[i] = authorities.get(i).getAuthority();
    }

    String token =
        JWT.create()
            .withClaim("id", id)
            .withClaim("email", email)
            .withClaim("profile", profile.getName())
            .withArrayClaim("role", roles)
            .withIssuer(JwtHelper.ISSUER)
            .withExpiresAt(Date.from(Instant.now().plus(Duration.ofHours(hours))))
            .sign(algorithm);

    return new Token(email, token);
  }

  public boolean verifyToken(String token) {
    if (token == null) {
      return false;
    }
    try {
      verifier.verify(token);
      return true;
    } catch (JWTVerificationException e) {
      return false;
    }
  }

  public boolean isStartWithBearer(String authorization) {
    return authorization.startsWith("Bearer ");
  }

  public List<SimpleGrantedAuthority> getRolesFromToken(String token)
      throws JWTVerificationException {

    DecodedJWT jwt = verifier.verify(token);
    Claim role = jwt.getClaim("role");
    String[] roles = role.asArray(String.class);

    List<SimpleGrantedAuthority> authorityList = new ArrayList<>();

    for (String item : roles) {
      authorityList.add(new SimpleGrantedAuthority(item));
    }
    return authorityList;
  }

  public String getEmailFromToken(String token) throws JWTVerificationException {

    DecodedJWT jwt = verifier.verify(token);
    Claim email = jwt.getClaim("email");

    return email.as(String.class);
  }

  public String getIdFromToken(String token) throws JWTVerificationException {

    DecodedJWT jwt = verifier.verify(token);
    Claim id = jwt.getClaim("id");

    return id.as(String.class);
  }

  public boolean verifyTokenCache(String token) {
    try {
      String id = this.getIdFromToken(token);
      return Optional.ofNullable(tokenCache.get(id))
          .flatMap(
              token1 -> {
                log.debug("Load token from cache for user with id {}", id);
                return Optional.of(token.equals(token1.getTokenString()));
              })
          .orElse(false);

    } catch (JWTVerificationException e) {
      return false;
    }
  }

  public Token sanitizeHeaderToken(String token) {
    if (token == null) {
      return null;
    }

    if (token.startsWith("Bearer ")) {
      String tokenString = token.substring(7);
      return new Token(getEmailFromToken(tokenString), tokenString);
    }
    throw new InvalidTokenFormatException();
  }

  public TokenDecrypted fromServerRequest(ServerRequest serverRequest) {
    String token =
        serverRequest.headers().header("Authorization").get(0); // TODO verify if get(0) is safe
    return toTokenDecrypted(sanitizeHeaderToken(token));
  }

  public TokenDecrypted toTokenDecrypted(Token token) {
    return TokenDecrypted.builder()
        .rawToken(token.getTokenString())
        .userId(getIdFromToken(token.getTokenString()))
        .build();
  }
}
