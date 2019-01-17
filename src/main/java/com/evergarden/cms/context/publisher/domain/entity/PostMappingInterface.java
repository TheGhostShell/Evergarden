package com.evergarden.cms.context.publisher.domain.entity;

import org.davidmoten.rx.jdbc.annotations.Column;

public interface PostMappingInterface {
	
	@Column("id")
	Long getId();
	
	@Column("title")
	String getTitle();
	
	@Column("body")
	String getBody();
	
	@Column("author")
	String getAuthor();
}
