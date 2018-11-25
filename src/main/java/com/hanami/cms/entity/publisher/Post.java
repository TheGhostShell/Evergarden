package com.hanami.cms.entity.publisher;

import javax.persistence.*;

@Entity
@Table
public class Post {

	@Id
	@GeneratedValue
	private Integer id;

	@Column
	private String title;

	@Column
	private String body;

	@Column
	private String author;

	public Post(String title, String body, String author) {
		this.title = title;
		this.body = body;
		this.author = author;
	}
}
