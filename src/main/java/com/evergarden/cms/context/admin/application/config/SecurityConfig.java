package com.evergarden.cms.context.admin.application.config;

import com.evergarden.cms.context.admin.domain.security.EvergardenAuthenticationManager;
import com.evergarden.cms.context.admin.domain.security.SecurityContextRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {

    private EvergardenAuthenticationManager authenticationManager;

    private SecurityContextRepository securityContextRepository;

    @Autowired
    public SecurityConfig(EvergardenAuthenticationManager authenticationManager, SecurityContextRepository securityContextRepository) {
        this.authenticationManager = authenticationManager;
        this.securityContextRepository = securityContextRepository;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        http
            .authorizeExchange().pathMatchers("/v1/private/**").hasRole("ADMIN")
            .and().authorizeExchange().pathMatchers("/v1/private/**").authenticated()
            .and().authorizeExchange().pathMatchers("/v1/guest").permitAll()
            .and().authorizeExchange().pathMatchers("/v1/login").permitAll()
            .and().authorizeExchange().pathMatchers("/v1/**").hasRole("GUEST")
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
