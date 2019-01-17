package com.evergarden.cms.context.publisher.infrastructure.persistence;

import com.evergarden.cms.context.publisher.domain.entity.Post;
import com.evergarden.cms.context.publisher.domain.entity.UpdatedPost;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

import static org.junit.Assert.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class PostRepositoryTest {
	
	@Autowired
	private PostRepository postRepository;
	
	/**
	 * Insert tree post in evergarden_post table and try to retrieve all the post
	 * with {@link PostRepository#fetchAll()}
	 */
	@Test
	@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/db/migration/V2018.01.15.17.09.50__init.sql")
	@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/db/script/createPost.sql")
	@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "/db/script/drop.sql")
	public void fetchAll() {
		StepVerifier.create(postRepository.fetchAll())
			.expectNextMatches(post -> {
				Assert.assertEquals("john", post.getAuthor());
				Assert.assertEquals("<b>This is a wonderfull book I ever read</b>", post.getBody());
				Assert.assertEquals("<h1>The book ever</h1>", post.getTitle());
				return true;
			}).expectNextCount(2).expectComplete().verify();
	}
	
	/**
	 * The fetchAll test will create the first post we try to retrieve the post write by john
	 */
	@Test
	@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/db/migration/V2018.01.15.17.09.50__init.sql")
	@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/db/script/createPost.sql")
	@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "/db/script/drop.sql")
	public void fetchById() {
		StepVerifier.create(postRepository.fetchById(1L))
			.expectNextMatches(post -> {
				Assert.assertEquals("john", post.getAuthor());
				Assert.assertEquals("<b>This is a wonderfull book I ever read</b>", post.getBody());
				Assert.assertEquals("<h1>The book ever</h1>", post.getTitle());
				return true;
			})
			.verifyComplete();
	}
	
	@Test
	@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/db/migration/V2018.01.15.17.09.50__init.sql")
	@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/db/script/createPost.sql")
	@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "/db/script/drop.sql")
	public void create() {
		Post newPost = new Post(
			"Violet",
			"Violet it's a flower name",
			"Batou-Ranger"
		);
		postRepository.create(newPost).map(postMappingInterface -> {
			StepVerifier.create(postRepository.fetchById(4L))
				.expectNextMatches(post -> {
					assertEquals("Batou-Ranger", post.getAuthor());
					assertEquals("Violet it's a flower name", post.getBody());
					assertEquals("Violet", post.getTitle());
					return true;
				})
				.verifyComplete();
			assertEquals(4L,  postMappingInterface.getId().longValue());
			return postMappingInterface;
		}).block();
	}
	
	@Test
	@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/db/migration/V2018.01.15.17.09.50__init.sql")
	@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/db/script/createPost.sql")
	@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "/db/script/drop.sql")
	public void update() {
		
		UpdatedPost upPost = new UpdatedPost();
		
		upPost.setAuthor("Lorem");
		upPost.setBody("Ip");
		upPost.setTitle("Sum");
		upPost.setId(3L);
		
		postRepository.update(upPost).block();
		
		StepVerifier.create(postRepository.fetchById(3L))
			.expectNextMatches(post -> {
				Assert.assertEquals("Lorem", post.getAuthor());
				Assert.assertEquals("Ip", post.getBody());
				Assert.assertEquals("Sum", post.getTitle());
				return true;
			})
			.verifyComplete();
	}
	
	@Test
	@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/db/migration/V2018.01.15.17.09.50__init.sql")
	@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/db/script/createPost.sql")
	@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "/db/script/drop.sql")
	public void delete() {
		postRepository.delete(3).block();
		
		StepVerifier.create(postRepository.fetchAll())
			.expectNextCount(2)
			.verifyComplete();
	}
}