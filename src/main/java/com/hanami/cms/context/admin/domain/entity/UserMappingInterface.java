package com.hanami.cms.context.admin.domain.entity;

import org.davidmoten.rx.jdbc.annotations.Column;

import java.util.Collection;
import java.util.List;

public interface UserMappingInterface {
	
	@Column("id")
	int getId();
	
	@Column("email")
	String getEmail();
	
	@Column("password")
	String getPassword();
	
	@Column("salt")
	String getSalt();
	
	@Column("firstname")
	String getFirstName();
	
	@Column("lastname")
	String getLastName();
	
	@Column("pseudo")
	String getPseudo();

	Collection<Role> getRoles();
	
	@Column("activated")
	boolean isActivated();

	User addRole(Role role);
}
