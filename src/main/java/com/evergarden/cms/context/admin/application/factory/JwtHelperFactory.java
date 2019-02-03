package com.evergarden.cms.context.admin.application.factory;

import com.evergarden.cms.context.admin.domain.security.JwtHelper;
import com.evergarden.cms.context.admin.domain.security.JwtRequest;
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
