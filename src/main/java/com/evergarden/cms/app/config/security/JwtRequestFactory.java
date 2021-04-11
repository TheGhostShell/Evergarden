package com.evergarden.cms.app.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class JwtRequestFactory {

  private Environment env;

  @Autowired
  public JwtRequestFactory(Environment env) {
    this.env = env;
  }

  @Bean
  public JwtRequest create() {
    return new JwtRequest(env.getProperty("jwt.secret"));
  }
}
