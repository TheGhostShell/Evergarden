package com.evergarden.cms.context.publisher.infrastructure.controller;

public class PostRequestTest {
	
	public Long id;
	
	public String body;
	
	public String author;
	
	public String title;
	
	public PostRequestTest(Long id, String body, String author, String title) {
		this.id = id;
		this.body = body;
		this.author = author;
		this.title = title;
	}
	
	public Long getId() {
		return id;
	}
	
	public String getBody() {
		return body;
	}
	
	public String getAuthor() {
		return author;
	}
	
	public String getTitle() {
		return title;
	}
}
