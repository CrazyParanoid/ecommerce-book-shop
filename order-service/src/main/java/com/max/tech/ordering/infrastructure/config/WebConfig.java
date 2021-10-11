package com.max.tech.ordering.infrastructure.config;

import com.max.tech.ordering.web.security.Role;
import com.max.tech.ordering.web.security.User;
import com.max.tech.ordering.web.security.UserAuthentication;
import com.nimbusds.jose.shaded.json.JSONArray;
import com.nimbusds.jose.shaded.json.JSONObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.hateoas.client.LinkDiscoverers;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.mediatype.collectionjson.CollectionJsonLinkDiscoverer;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.plugin.core.SimplePluginRegistry;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.jwt.Jwt;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
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
                    .map(r -> new Role(
                            r.toString().toUpperCase(Locale.ROOT)))
                    .collect(Collectors.toList());

            return new UserAuthentication(new User(jwt.getSubject(), roles));
        };
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .securityContexts(List.of(securityContext()))
                .securitySchemes(List.of(new ApiKey("JWT", AUTHORIZATION, "header")))
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }

    @Bean
    public LinkDiscoverers discoverers() {
        return new LinkDiscoverers(SimplePluginRegistry.of(List.of(new CollectionJsonLinkDiscoverer())));
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .build();
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Collections.singletonList(new SecurityReference("JWT", authorizationScopes));
    }

}
