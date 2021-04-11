package com.evergarden.cms.context.publisher.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

@Document
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
// Todo prefer value object and dont use @Data but @Getter
public class Post {

    @Id
    private String        id;
    private String        title;
    private String        body;
    private String        authorId;
    private String        summary;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String        updatedByUserId;
    private Set<Tag>      tags;
}
