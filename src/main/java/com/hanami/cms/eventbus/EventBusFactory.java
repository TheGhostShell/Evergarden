package com.hanami.cms.eventbus;

import com.google.common.eventbus.EventBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
public class EventBusFactory {

	@Bean
	@Scope("Singleton")
	public EventBus getEventBusInstance()
	{
		return new EventBus();
	}
}
