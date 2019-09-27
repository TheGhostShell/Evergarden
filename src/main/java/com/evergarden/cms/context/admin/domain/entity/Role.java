package com.evergarden.cms.context.admin.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.NonNull;

@EqualsAndHashCode
@NoArgsConstructor
@Document
public class Role {

    @Id
    private String id;

    @Indexed(unique = true)
    @NonNull
    private String role;

    public Role(String role) {
        setRole(role);
    }

    // Factory
    public static Role createFromRawValue(String role) {
        return new Role("").setRoleFromRawValue(role);
    }
    
    public static Role createFromRawValue(String id, String role) {
        return new Role("").setRoleFromRawValue(role).setId(id);
    }
    
    
    private Role setRoleFromRawValue(String role) {
        this.role = role;
        return this;
    }

    // Setter
    public Role setRole(String role) {
        this.role = "ROLE_" + role.toUpperCase();
        return this;
    }

    public Role setId(String id) {
        this.id = id;
        return this;
    }

    // Getter

    public String getRoleValue() {
        return role;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return role;
    }
}
