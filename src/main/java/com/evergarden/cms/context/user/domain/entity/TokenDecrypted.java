package com.evergarden.cms.context.user.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class TokenDecrypted {
    private String rawToken;
    private String userId;
}
