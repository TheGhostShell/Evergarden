package com.evergarden.cms.context.publisher.domain.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PostTest {

	@Test
	void createInstance() {

		Post post = Post.builder()
            .title("title")
            .authorId("authorId")
            .body("body post")
            .build();

		assertNull( post.getId());
		assertEquals("title", post.getTitle());
		assertEquals("authorId", post.getAuthorId());
		assertEquals("body post", post.getBody());

		post.setId("postId");
		post.setAuthorId("author2");
		post.setTitle("title2");
		post.setBody("body post2");

		assertEquals("postId", post.getId());
		assertEquals("title2", post.getTitle());
		assertEquals("author2", post.getAuthorId());
		assertEquals("body post2", post.getBody());
	}
}
