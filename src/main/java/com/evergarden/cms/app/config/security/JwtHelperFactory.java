package com.evergarden.cms.app.config.security;

import com.evergarden.cms.context.user.domain.entity.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.Cache;

@Configuration
public class JwtHelperFactory {

  private final JwtRequest jwtRequest;
  private final Cache<String, Token> tokenCache;

  @Autowired
  public JwtHelperFactory(JwtRequest jwtRequest, Cache<String, Token> tokenCache) {
    this.jwtRequest = jwtRequest;
    this.tokenCache = tokenCache;
  }

  @Bean
  public JwtHelper jwtHelperInstance() {
    return new JwtHelper(jwtRequest, tokenCache);
  }
}
