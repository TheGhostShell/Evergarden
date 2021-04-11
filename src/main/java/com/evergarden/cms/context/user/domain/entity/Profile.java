package com.evergarden.cms.context.user.domain.entity;

import com.evergarden.cms.context.user.domain.exception.InvalidProfileException;
import com.mongodb.lang.NonNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Document
@NoArgsConstructor
@Builder
@Getter
@EqualsAndHashCode
public class Profile {

    @Id
    private String id;

    @NonNull
    @Indexed(unique = true)
    private String name;

    @DBRef
    @Builder.Default
    private List<Role> roles = new ArrayList<>();

    @Builder
    public Profile(String id, String name, List<Role> roles) {
        this.id = id;
        this.roles = roles;
        this.setName(name);
    }

    public Profile addRole(Role role) {
        if (roles == null) {
            roles = new ArrayList<>();
        }
        roles.add(role);
        return this;
    }

    public void clearRole(){
        roles.clear();
    }

    public void setName(@NonNull String name) {
        if(!name.matches("[a-zA-Z_]+")){
            throw new InvalidProfileException(name);
        }
        this.name = name.toUpperCase();
    }
}
