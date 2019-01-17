package com.evergarden.cms;

import com.evergarden.cms.context.publisher.infrastructure.persistence.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class CmsApplicationTests {
	
	@Autowired
	private PostRepository postRepository;
	
	/*@Test
	public void contextLoads() {
	}*/
	
	@Test
	public void testStepVerifier() {
		/*Flux<Long> flux = Flux.interval(Duration.ofSeconds(1L)).take(10L);
		
		Duration dur = Duration.ofSeconds(1L);
		
		Long value  = dur.getSeconds();
		System.out.println(value.intValue());
		
		Duration duration = StepVerifier.withVirtualTime(() -> Flux.interval(Duration.ofSeconds(10L)).take(2L))
			.thenAwait(Duration.ofSeconds(40L))
			.expectNextCount(2)
			.expectComplete()
			.verify(Duration.ofSeconds(4L));
		
		System.out.println(duration.getSeconds());*/

//		StepVerifier.withVirtualTime(()->flux)
//			.expectSubscription()
//			.expect
	}
}
