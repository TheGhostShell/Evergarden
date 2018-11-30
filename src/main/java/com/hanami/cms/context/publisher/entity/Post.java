package com.hanami.cms.context.publisher.entity;

import javax.persistence.*;

@Entity
@Table
public class Post implements PostMappingInterface {

    @Id
    @GeneratedValue
    private int id;

    @Column
    private String title;

    @Column(columnDefinition = "text")
    private String body;

    @Column
    private String author;

    public Post(String title, String body, String author) {
        this.title = title;
        this.body = body;
        this.author = author;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getAuthor() {
        return author;
    }

    public static Post empty() {
        return new Post("", "", "");
    }
}
