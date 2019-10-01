package com.evergarden.cms.context.router.container;

import com.evergarden.cms.context.router.RouteBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouterBuilderFactory {

	@Bean
	public RouteBuilder routeBuilder() {
		return new RouteBuilder();
	}
}
