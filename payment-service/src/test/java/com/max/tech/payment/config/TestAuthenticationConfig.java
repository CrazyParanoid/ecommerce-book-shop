package com.max.tech.payment.config;

import com.max.tech.payment.TestValues;
import com.max.tech.payment.web.security.User;
import com.max.tech.payment.web.security.UserAuthentication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import java.util.List;

@Profile("security")
@Configuration
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
