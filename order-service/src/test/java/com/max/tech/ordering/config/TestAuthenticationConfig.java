package com.max.tech.ordering.config;

import com.max.tech.ordering.util.TestValues;
import com.max.tech.ordering.web.security.User;
import com.max.tech.ordering.web.security.UserAuthentication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import java.util.List;

@Configuration
@Profile("security")
public class TestAuthenticationConfig {

    @Bean
    public AuthenticationManager authenticationManager() {
        return new SampleAuthenticationManager();
    }

    public static class SampleAuthenticationManager implements AuthenticationManager {

        public Authentication authenticate(Authentication auth) {
            return new UserAuthentication(
                    new User(
                            TestValues.CLIENT_ID,
                            List.of(new User.Role("ADMIN"))
                    )
            );
        }
    }
}
