package com.evergarden.cms.context.user.domain.entity;

import com.evergarden.cms.context.user.domain.exception.InvalidRoleNameException;
import com.evergarden.cms.context.user.domain.security.Permission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@Document
@Builder
@AllArgsConstructor
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
        if(! role.matches("(?i).*(ROLE).*")){
            throw new InvalidRoleNameException(role);
        }
        // TODO if not uppercase role throw InvalidRoleNameException
        return new Role("").setRoleFromRawValue(role);
    }

    private Role setRoleFromRawValue(String role) {
        this.role = role;
        return this;
    }

    public Role setRole(String role) {
        if(role.matches("(?i).*(ROLE).*")){
            this.role = role.toUpperCase();
            return this;
        }
        this.role = "ROLE_" + role.toUpperCase();
        return this;
    }

    public Role setId(String id) {
        this.id = id;
        return this;
    }

    @Override
    public String toString() {
        return role;
    }
}
