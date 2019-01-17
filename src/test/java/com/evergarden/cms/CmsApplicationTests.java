package com.evergarden.cms;

import com.evergarden.cms.context.publisher.infrastructure.persistence.PostRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

@RunWith(SpringRunner.class)
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
	
	@Test
	@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/db/script/insertAdmin.sql")
	public void testCapitalise() {
		StepVerifier.create(postRepository.fetchById(1))
			.expectNextMatches(postMappingInterface -> {
				Assert.assertEquals("john", postMappingInterface.getAuthor());
				return true;
			})
			.verifyComplete();
	}
}
