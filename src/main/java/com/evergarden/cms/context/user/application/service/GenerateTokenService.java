package com.evergarden.cms.context.user.application.service;

import com.evergarden.cms.context.user.domain.entity.EncodedCredential;
import com.evergarden.cms.context.user.domain.entity.Token;
import com.evergarden.cms.context.user.infrastructure.controller.input.UnAuthUser;
import com.evergarden.cms.context.user.domain.exception.InvalidCredentialException;
import com.evergarden.cms.app.config.security.EvergardenEncoder;
import com.evergarden.cms.app.config.security.JwtHelper;
import com.evergarden.cms.context.user.infrastructure.persistence.UserRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.cache.Cache;
import java.util.ArrayList;

@Service
public class GenerateTokenService {

  private UserRepository userRepository;
  private Environment env;
  private Logger logger;
  private JwtHelper jwtHelper;
  private Cache<String, Token> tokenCache;

  @Autowired
  public GenerateTokenService(
      UserRepository userRepository,
      Environment env,
      Logger logger,
      JwtHelper jwtHelper,
      Cache<String, Token> tokenCache) {
    this.userRepository = userRepository;
    this.env = env;
    this.logger = logger;
    this.jwtHelper = jwtHelper;
    this.tokenCache = tokenCache;
  }

  public Mono<Token> generateToken(UnAuthUser unAuthUser) {
    return Mono.just(unAuthUser)
        .flatMap(unAuthU -> userRepository.findByEmail(unAuthU.getEmail()))
        .flatMap(
            user -> {
              EncodedCredential encodedCredential =
                  new EncodedCredential(user.getSalt(), user.getPassword());
              EvergardenEncoder encoder = new EvergardenEncoder(env, encodedCredential);

              boolean isValidPass = encoder.matches(unAuthUser.getPassword(), user.getPassword());
              logger.debug("Is password valid for user " + user.getEmail() + " " + isValidPass);

              if (isValidPass) {
                ArrayList<SimpleGrantedAuthority> authorities = new ArrayList();
                user.getProfile()
                    .getRoles()
                    .forEach(
                        role -> {
                          authorities.add(new SimpleGrantedAuthority(role.getRole()));
                        });

                logger.debug("Try to generate token");

                // Token is cached for 6 hours
                Token token =
                    jwtHelper.generateToken(
                        user.getEmail(), authorities, user.getId(), user.getProfile());
                logger.debug("Token is generated with this value " + token.getTokenString());

                return Mono.just(token);
              }

              return Mono.error(new InvalidCredentialException(unAuthUser.getEmail()));
            });
  }

  public Boolean removeTokenFromCache(String id) {
    return tokenCache.remove(id);
  }
}
