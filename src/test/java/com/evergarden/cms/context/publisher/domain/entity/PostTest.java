package com.evergarden.cms.context.publisher.domain.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PostTest {
	
	@Test
	void createInstance() {
		
		Post post = new Post("title", "body post", "author");
		assertNull( post.getId());
		assertEquals("title", post.getTitle());
		assertEquals("author", post.getAuthor());
		assertEquals("body post", post.getBody());
		
		post.setId(1L);
		post.setAuthor("author2");
		post.setTitle("title2");
		post.setBody("body post2");
		
		assertEquals(1L, post.getId().longValue());
		assertEquals("title2", post.getTitle());
		assertEquals("author2", post.getAuthor());
		assertEquals("body post2", post.getBody());
	}
}