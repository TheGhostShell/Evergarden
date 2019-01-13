package com.hanami.cms.context.admin.domain.entity;

import org.davidmoten.rx.jdbc.annotations.Column;

public interface GuestMappingInterface {

    @Column("token")
    public String getToken();

    @Column("subject")
    public String getSubject();
}
