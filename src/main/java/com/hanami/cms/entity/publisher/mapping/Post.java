package com.hanami.cms.entity.publisher.mapping;

import org.davidmoten.rx.jdbc.annotations.Column;

public interface Post {
	
	@Column("id")
	int getId();
	
	@Column("title")
	String getTitle();
	
	@Column("body")
	String getBody();
	
	@Column("author")
	String getAuthor();
}
