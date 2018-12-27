package com.hanami.cms.context.admin.domain.entity;

import org.davidmoten.rx.jdbc.annotations.Column;

public interface UserMappingInterface {
	
	@Column("id")
	int getId();
	
	@Column("email")
	String getEmail();
	
	@Column("password")
	String getPassword();
	
	@Column("firstname")
	String getFirstname();
	
	@Column("lastname")
	String getLastname();
	
	@Column("role")
	Role getRole();
	
	@Column("activated")
	boolean isActivated();
}
