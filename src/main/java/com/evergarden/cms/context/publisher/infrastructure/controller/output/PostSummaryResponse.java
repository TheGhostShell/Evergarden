package com.evergarden.cms.context.publisher.infrastructure.controller.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class PostSummaryResponse {
    private String id;
    private String summary;
    private String title;
    private String authorId;
    private String authorFullName;
}
