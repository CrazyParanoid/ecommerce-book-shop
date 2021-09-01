package com.max.tech.ordering.web.security;

import org.springframework.security.core.GrantedAuthority;

public record Role(String value) implements GrantedAuthority {
    @Override
    public String getAuthority() {
        return this.value;
    }
}
