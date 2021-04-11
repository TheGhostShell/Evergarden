package com.evergarden.cms.context.publisher.infrastructure.controller.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class UnSavePostRequest {
    private String title;
    private String body;
    private String summary;
}
