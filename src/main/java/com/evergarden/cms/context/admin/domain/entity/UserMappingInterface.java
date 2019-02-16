package com.evergarden.cms.context.admin.domain.entity;

import org.davidmoten.rx.jdbc.annotations.Column;

import java.util.Collection;

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
	String getFirstname();
	
	@Column("lastname")
	String getLastname();
	
	@Column("pseudo")
	String getPseudo();

	Collection<Role> getRoles();
	
	@Column("activated")
	boolean isActivated();

	User addRole(Role role);
}
