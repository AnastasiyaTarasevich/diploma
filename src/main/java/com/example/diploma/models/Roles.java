package com.example.diploma.models;

import org.springframework.security.core.GrantedAuthority;

public enum Roles implements GrantedAuthority

{
    ADMIN, USER, SUPPLIER, LOGISTICS;

    @Override
    public String getAuthority() {
        return name();
    }
}
