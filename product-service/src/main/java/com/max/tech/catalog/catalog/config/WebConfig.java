package com.max.tech.catalog.catalog.config;

import com.max.tech.catalog.catalog.web.User;
import com.max.tech.catalog.catalog.web.UserAuthentication;
import com.nimbusds.jose.shaded.json.JSONArray;
import com.nimbusds.jose.shaded.json.JSONObject;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.jwt.Jwt;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableDiscoveryClient
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebConfig extends WebSecurityConfigurerAdapter {
    private static final String REALM_ACCESS = "realm_access";
    private static final String ROLES = "roles";

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests(expressionInterceptUrlRegistry
                -> expressionInterceptUrlRegistry.anyRequest().authenticated())
                .oauth2ResourceServer(resourceServerConfigurer ->
                        resourceServerConfigurer.jwt(jwtConfigurer ->
                                jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter())));
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/actuator/**",
                "/v2/api-docs",
                "/v3/api-docs",
                "/swagger-resources/**",
                "/swagger-ui/**",
                "/webjars/**");
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        jsonConverter.setDefaultCharset(StandardCharsets.UTF_8);
        return jsonConverter;
    }

    private Converter<Jwt, UserAuthentication> jwtAuthenticationConverter() {
        return jwt -> {
            var realmAccess = (JSONObject) jwt.getClaims().get(REALM_ACCESS);
            var roles = ((JSONArray) realmAccess.get(ROLES)).stream()
                    .map(r -> new User.Role(
                            r.toString().toUpperCase(Locale.ROOT)))
                    .collect(Collectors.toList());

            return new UserAuthentication(new User(jwt.getSubject(), roles));
        };
    }

}
