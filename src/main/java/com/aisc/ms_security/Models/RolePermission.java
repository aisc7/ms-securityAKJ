package com.aisc.ms_security.Models;

import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.DBRef;

@Getter
public class RolePermission {
    private String _id;
    @DBRef
    private Role role;
    @DBRef
    private Permission permission;

    public RolePermission() {

    }


    public void setRole(Role role) {
        this.role = role;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }
}
