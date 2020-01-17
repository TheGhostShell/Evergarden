package com.evergarden.cms.context.user.domain.entity;

import com.mongodb.lang.NonNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

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

    private Avatar avatar;

    private boolean activated = true;

    @DBRef
    private Collection<Role> roles = new ArrayList<>();

    private String salt;

    @Transient
    private EncodedCredential encodedCredential;

    public User setEncodedCredential(EncodedCredential encodedCredential) {
        password = encodedCredential.getEncodedPassword();
        salt = encodedCredential.getSalt();

        this.encodedCredential = encodedCredential;

        return this;
    }

    public User addRole(Role role) {
        if (roles == null) roles = new ArrayList<>();
        roles.add(role);
        return this;
    }

    public boolean clearRole(){
        roles.clear();
        return roles.size() == 0;
    }
}
