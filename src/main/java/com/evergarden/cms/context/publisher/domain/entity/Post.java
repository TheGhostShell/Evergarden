package com.evergarden.cms.context.publisher.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;

@Document
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Post {

    @Id
    private String id;
    private String title;
    private String body;
    private String author;
}
