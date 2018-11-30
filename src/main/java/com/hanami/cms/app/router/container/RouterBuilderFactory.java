package com.hanami.cms.app.router.container;

import com.hanami.cms.app.router.RouteBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouterBuilderFactory {

	@Bean
	public RouteBuilder routeBuilder() {
		return new RouteBuilder();
	}
}
