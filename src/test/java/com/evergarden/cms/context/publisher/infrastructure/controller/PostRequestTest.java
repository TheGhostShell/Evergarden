package com.evergarden.cms.context.publisher.infrastructure.controller;

public class PostRequestTest {
	
	public Long id;
	
	public String body;
	
	public String author;
	
	public String title;
	
	public PostRequestTest(String body, String author, String title) {
		this.body = body;
		this.author = author;
		this.title = title;
	}

	public PostRequestTest setId(Long id) {
		this.id = id;
		return this;
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
