package com.evergarden.cms.context.user.domain.entity;

import com.mongodb.lang.NonNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Never expose setter for password and salt fields
 * Create a custom builder for encodedCredential
 */
@ToString
@Document
@NoArgsConstructor
public class User {

    @Id
    @Getter
    private String id;

    @Indexed(unique = true)
    @NonNull
    @Getter
    @Setter
    private String email;

    @NonNull
    @Getter
    private String password;

    @Getter
    @Setter
    private String firstname;

    @Getter
    @Setter
    private String lastname;

    @Getter
    @Setter
    private String pseudo;

    @Getter
    @Setter
    private Avatar avatar;

    @Setter
    @Getter
    private boolean activated = true;

    @DBRef
    @Setter
    @Getter
    private Profile profile;

    @Getter
    private String salt;

    @Transient
    @Getter
    private EncodedCredential encodedCredential;

    @Builder
    public User(String id, @NonNull String email, String firstname, String lastname, String pseudo, Avatar avatar,
                boolean activated, Profile profile, EncodedCredential encodedCredential) {
        this.id = id;
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.pseudo = pseudo;
        this.avatar = avatar;
        this.activated = activated;
        this.profile = profile;
        this.setEncodedCredential(encodedCredential);
    }

    public User setEncodedCredential(EncodedCredential encodedCredential) {
        password = encodedCredential.getEncodedPassword();
        salt = encodedCredential.getSalt();

        this.encodedCredential = encodedCredential;

        return this;
    }
}
