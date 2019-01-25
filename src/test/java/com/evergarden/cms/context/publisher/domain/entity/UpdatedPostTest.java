package com.evergarden.cms.context.publisher.domain.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UpdatedPostTest {
	
	@Test
	void createInstance() {
		
		UpdatedPost post = new UpdatedPost(1L, "title", "author", "body post");
		assertEquals(1L, post.getId().longValue());
		assertEquals("title", post.getTitle());
		assertEquals("author", post.getAuthor());
		assertEquals("body post", post.getBody());
		
		UpdatedPost postNoArgsConstructor = new UpdatedPost();
		postNoArgsConstructor.setId(1L);
		postNoArgsConstructor.setAuthor("author");
		postNoArgsConstructor.setTitle("title");
		postNoArgsConstructor.setBody("body post");
		
		assertEquals(1L, postNoArgsConstructor.getId().longValue());
		assertEquals("title", postNoArgsConstructor.getTitle());
		assertEquals("author", postNoArgsConstructor.getAuthor());
		assertEquals("body post", postNoArgsConstructor.getBody());
	}
	
}