package com.evergarden.cms.context.publisher.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.*;
import java.util.UUID;

@Document
@Data
@AllArgsConstructor
public class Post {

    @Id
    private String id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("body")
    private String body;

    @JsonProperty("author")
    private String author;

    public Post(String title, String body, String author) {
        this.title = title;
        this.body = body;
        this.author = author;
    }

    /**
     * Return an empty post
     *
     * @return a new instance of post with all value set to null
     */
    public static Post empty() {
        return new Post("", "", "");
    }
}
