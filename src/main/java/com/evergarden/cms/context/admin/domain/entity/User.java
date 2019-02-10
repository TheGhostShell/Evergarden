package com.evergarden.cms.context.admin.domain.entity;

import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Entity
@Table(name = "evergarden_user")
@ToString
public class User implements UserMappingInterface {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT")
    private int id;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Column
    private String firstname;
    
    @Column
    private String lastname;
    
    @Column
    private String pseudo;
    
    @Column(nullable = false)
    private boolean activated = true;
    
    @ManyToMany
    @JoinTable(name = "evergarden_user_roles",
        joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Collection<Role> roles = new ArrayList<>();
    
    @Column(nullable = false)
    private String salt;
    
    @Transient
    private EncodedCredential encodedCredential;
    
    // Setter
    
    public User setEncodedCredential(EncodedCredential encodedCredential) {
        password = encodedCredential.getEncodedPassword();
        salt = encodedCredential.getSalt();
        
        this.encodedCredential = encodedCredential;
        
        return this;
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
    
    public User addRole(Role role) {
        this.roles.add(role);
        return this;
    }
    
    public User setId(int id) {
        this.id = id;
        return this;
    }
    
    public User setPseudo(String pseudo) {
        this.pseudo = pseudo;
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
    public String getPseudo() {
        return pseudo;
    }
    
    @Override
    public boolean isActivated() {
        return activated;
    }
    
    @Override
    public Collection<Role> getRoles() {
        return roles;
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
