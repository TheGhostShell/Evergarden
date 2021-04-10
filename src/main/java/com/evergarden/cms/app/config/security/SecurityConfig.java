package com.evergarden.cms.app.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {

  private final EvergardenAuthenticationManager authenticationManager;
  private final SecurityContextRepository       securityContextRepository;
  private final Environment                     env;

  @Autowired
  public SecurityConfig(
      EvergardenAuthenticationManager authenticationManager,
      SecurityContextRepository securityContextRepository,
      Environment env) {
    this.authenticationManager = authenticationManager;
    this.securityContextRepository = securityContextRepository;
    this.env = env;
  }

  @Bean
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        http
            .authorizeExchange().pathMatchers(env.getProperty("v1s") + "/**").hasRole("ADMIN")
            .and().authorizeExchange().pathMatchers(env.getProperty("v1s") + "/**").authenticated()
            .and().authorizeExchange().pathMatchers(env.getProperty("v1") + "/guest").permitAll()
            .and().authorizeExchange().pathMatchers(env.getProperty("v1") + "/login").permitAll()
            .and().authorizeExchange().pathMatchers(env.getProperty("v1") + "/test").permitAll()
            .and().authorizeExchange().pathMatchers(env.getProperty("v1") + "/**").hasRole("GUEST")
            .and().authenticationManager(authenticationManager)
            .securityContextRepository(securityContextRepository)
            .csrf().disable()
            .formLogin().disable()
            .httpBasic().disable()
            .authorizeExchange()
            .pathMatchers("/**")
            .permitAll();

    return http.build();
  }
}
