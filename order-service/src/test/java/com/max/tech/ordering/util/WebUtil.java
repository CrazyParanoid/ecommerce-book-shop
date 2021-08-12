package com.max.tech.ordering.util;

import com.google.common.io.Files;
import com.max.tech.ordering.web.security.User;
import com.max.tech.ordering.web.security.UserAuthentication;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.mockito.Mockito;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ResourceUtils;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;

@UtilityClass
public class WebUtil {
    private final long EXPIRATION_TIME = 864_000_000;
    private final String SECRET = "Secret";

    public void mockSecurity() {
        var authentication = new UserAuthentication(
                new User(
                        TestValues.CLIENT_ID,
                        List.of(
                                new User.Role("ADMIN"))
                )
        );

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    public String createToken() {
        return Jwts.builder()
                .setSubject(TestValues.CLIENT_ID)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }

    @SneakyThrows
    public String getResponse(String path) {
        return Files.asCharSource(
                ResourceUtils.getFile(path),
                Charset.defaultCharset()
        ).read();
    }


}
