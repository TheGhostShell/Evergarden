package com.evergarden.cms.context.admin.domain.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Guest implements GuestMappingInterface {

    private String subject;

    private String token;

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public String getSubject() {
        return subject;
    }
}
