package com.evergarden.cms.context.user.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Avatar {

    private String filePath;

    private String relativeUri;

    private String fileName;

    // private String type;
}
