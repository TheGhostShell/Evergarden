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

    private int userId;

    // Setter

    public Role setRole(String role) {
        this.role = "ROLE_" + role.toUpperCase();
        return this;
    }

    // Getter

    public String getRoleValue() {
        return role;
    }
}
