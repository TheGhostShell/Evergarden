package com.evergarden.cms.context.publisher.domain.entity;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatedPost implements PostMappingInterface {

    private Long    id;
    private String title;
    private String author;
    private String body;

    public Long getId() {
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
