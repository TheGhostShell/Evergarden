package com.hanami.cms.context.admin.domain.entity;

import javax.persistence.*;

@Entity
@Table
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false)
    private String role;

    public Role(String role) {
        setRole(role);
    }

    // Factory

    public static Role createFromRawValue(String role) {
        return new Role("").setRoleFromRawValue(role);
    }
    
    public static Role createFromRawValue(int id, String role) {
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

    public Role setId(int id) {
        this.id = id;
        return this;
    }

    // Getter

    public String getRoleValue() {
        return role;
    }

    public int getId() {
        return id;
    }
}
