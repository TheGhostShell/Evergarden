package com.evergarden.cms.context.publisher.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "evergarden_post")
@Data
public class Post implements PostMappingInterface {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT")
    private Long id;

    @Column
    @JsonProperty("title")
    private String title;

    @Column(columnDefinition = "CLOB")
    @JsonProperty("body")
    private String body;

    @Column
    @JsonProperty("author")
    private String author;

    public Post(String title, String body, String author) {
        this.title = title;
        this.body = body;
        this.author = author;
    }

    public Long getId() {
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

    /**
     * Return an empty post
     *
     * @return a new instance of post with all value set to null
     */
    public static Post empty() {
        return new Post("", "", "");
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
