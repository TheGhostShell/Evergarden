package com.evergarden.cms.app.config.security;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtHelperFactory {

    private JwtRequest jwtRequest;
    private Logger logger;

    @Autowired
    public JwtHelperFactory(JwtRequest jwtRequest, Logger logger) {
        this.jwtRequest = jwtRequest;
        this.logger = logger;
    }

    @Bean
    public JwtHelper jwtHelperInstance() {
        return new JwtHelper(jwtRequest, logger);
    }
}
