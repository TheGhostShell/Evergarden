package com.hanami.cms;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CmsApplicationTests {

	@Test
	public void contextLoads() {
	}
	
	@Test
	public void testStepVerifier() {
		Flux<Long> flux = Flux.interval(Duration.ofSeconds(1L)).take(10L);
		
		Duration dur = Duration.ofSeconds(1L);
		
		Long value  = dur.getSeconds();
		System.out.println(value.intValue());
		
		Duration duration = StepVerifier.withVirtualTime(() -> Flux.interval(Duration.ofSeconds(10L)).take(2L))
			.thenAwait(Duration.ofSeconds(40L))
			.expectNextCount(2)
			.expectComplete()
			.verify(Duration.ofSeconds(4L));
		
		System.out.println(duration.getSeconds());
		
//		StepVerifier.withVirtualTime(()->flux)
//			.expectSubscription()
//			.expect
	}
	
	@Test
	public void testCapitalise() {
		String username = "john".toUpperCase();
	}
}
