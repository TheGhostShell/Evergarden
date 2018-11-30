package com.hanami.cms.config.logger;

import org.slf4j.Logger;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class LoggerFactory {

	@Bean
	@Scope("prototype")
	public Logger logger(InjectionPoint injectionPoint) throws NullPointerException{
		return org.slf4j.LoggerFactory.getLogger(injectionPoint.getMethodParameter().getContainingClass());
	}
}
