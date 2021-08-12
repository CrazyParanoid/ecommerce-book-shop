package com.max.tech.catalog.catalog.web;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import javax.security.auth.Subject;

public class UserAuthentication extends AbstractAuthenticationToken {
    private final User user;

    public UserAuthentication(User user) {
        super(user.getRoles());
        this.user = user;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.user;
    }

    @Override
    public boolean implies(Subject subject) {
        return super.implies(subject);
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }
}
