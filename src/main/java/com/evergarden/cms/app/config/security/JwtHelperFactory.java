package com.evergarden.cms.app.config.security;

import com.evergarden.cms.context.user.domain.entity.Token;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.Cache;

@Configuration
public class JwtHelperFactory {

    private JwtRequest jwtRequest;
    private Logger logger;
    private Cache<String, Token> tokenCache;

    @Autowired
    public JwtHelperFactory(JwtRequest jwtRequest, Logger logger, Cache<String, Token> tokenCache) {
        this.jwtRequest = jwtRequest;
        this.logger = logger;
        this.tokenCache = tokenCache;
    }

    @Bean
    public JwtHelper jwtHelperInstance() {
        return new JwtHelper(jwtRequest, logger, tokenCache);
    }
}
