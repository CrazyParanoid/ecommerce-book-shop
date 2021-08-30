package com.max.tech.ordering.web.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

@Getter
@AllArgsConstructor
public class User {
    public final static String SERVICE_USER_ID = "order-service";

    private final String id;
    private final List<Role> roles;

    @AllArgsConstructor
    public static class Role implements GrantedAuthority {
        private final String value;

        @Override
        public String getAuthority() {
            return this.value;
        }
    }

}
