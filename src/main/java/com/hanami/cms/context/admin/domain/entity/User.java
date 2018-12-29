package com.hanami.cms.context.admin.domain.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table
public class User implements UserMappingInterface, UserDetails {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(unique = true, nullable = false)
	private String email;
	
	@Column(nullable = false)
	private String password;
	
	@Column
	private String firstname;
	
	@Column
	private String lastname;
	
	@Column(nullable = false)
	private boolean activated = true;
	
	@Column
	private Role role;
	
	@Column(nullable = false)
	private String salt;
	
	@Transient
	private EncodedCredential encodedCredential;
	
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return null;
	}
	
	public boolean isAccountNonExpired() {
		return false;
	}
	
	public boolean isAccountNonLocked() {
		return false;
	}
	
	public boolean isCredentialsNonExpired() {
		return false;
	}
	
	public boolean isEnabled() {
		return false;
	}
	
	// Setter
	
	public void setEncodedCredential(EncodedCredential encodedCredential) {
		password = encodedCredential.getEncodedPassword();
		salt = encodedCredential.getSalt();
		
		this.encodedCredential = encodedCredential;
	}
	
	public User setEmail(String email) {
		this.email = email;
		return this;
	}
	
	public User setFirstname(String firstname) {
		this.firstname = firstname;
		return this;
	}
	
	public User setLastname(String lastname) {
		this.lastname = lastname;
		return this;
	}
	
	public User setActivated(boolean activated) {
		this.activated = activated;
		return this;
	}
	
	public User setRole(Role role) {
		this.role = role;
		return this;
	}
	
	// Getter
	
	@Override
	public int getId() {
		return id;
	}
	
	@Override
	public String getEmail() {
		return email;
	}
	
	public String getFirstName() {
		return firstname;
	}
	
	@Override
	public String getLastName() {
		return lastname;
	}
	
	@Override
	public boolean isActivated() {
		return activated;
	}
	
	@Override
	public Role getRole() {
		return role;
	}
	
	@Override
	public String getSalt() {
		return salt;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getUsername() {
		return email;
	}
	
	public EncodedCredential getEncodedCredential() {
		return encodedCredential;
	}
}
