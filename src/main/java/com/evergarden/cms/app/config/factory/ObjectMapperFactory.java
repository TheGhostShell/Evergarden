package com.evergarden.cms.app.config.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class ObjectMapperFactory {

    @Bean
    @Scope("singleton")
    public ObjectMapper objectMapperSingletonFactory() {
        return new ObjectMapper();
    }
}
