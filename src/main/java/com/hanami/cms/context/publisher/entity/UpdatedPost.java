package com.hanami.cms.context.publisher.entity;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatedPost implements PostMappingInterface {

    private int    id;
    private String title;
    private String author;
    private String body;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getBody() {
        return body;
    }
}
