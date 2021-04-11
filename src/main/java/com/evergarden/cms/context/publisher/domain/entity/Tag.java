package com.evergarden.cms.context.publisher.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tag {

    @Id
    private String id;
    private String tag;
}
