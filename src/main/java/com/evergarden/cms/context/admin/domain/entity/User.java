package com.evergarden.cms.context.admin.domain.entity;

import com.mongodb.lang.NonNull;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Collection;

@ToString
@Document
@Data
public class User implements UserMappingInterface {
    
    @Id
    private String id;

    @Indexed(unique = true)
    @NonNull
    private String email;

    @NonNull
    private String password;
    
    private String firstname;
    
    private String lastname;

    private String pseudo;

    private boolean activated = true;

    @DBRef
    private Collection<Role> roles = new ArrayList<>();
    
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
    
    public String getFirstname() {
        return firstname;
    }
    
    @Override
    public String getLastname() {
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
