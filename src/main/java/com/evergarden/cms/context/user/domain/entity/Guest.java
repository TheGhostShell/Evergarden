package com.evergarden.cms.context.user.domain.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Guest {

    private String subject;

    private String token;
}
