package com.max.tech.catalog.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Files;
import com.max.tech.catalog.TestProductFactory;
import com.max.tech.catalog.TestValues;
import com.max.tech.catalog.catalog.Application;
import com.max.tech.catalog.config.MongoConfig;
import com.max.tech.catalog.config.TestAuthenticationConfig;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.RsaProvider;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.ResourceUtils;

import java.nio.charset.Charset;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.util.Date;

@AutoConfigureMockMvc
@ActiveProfiles({"it", "security"})
@WithMockUser(authorities = "ADMIN")
@SpringBootTest(classes = Application.class)
@Import({MongoConfig.class, TestAuthenticationConfig.class})
public class ProductRestAPITest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @SneakyThrows
    public void test_post_new_product() {
        var actualResponse = postProduct();

        System.out.println(actualResponse);
        var expectedResponse = getResponse("classpath:json/product_response.json");
        JSONAssert.assertEquals(
                expectedResponse, actualResponse,
                new CustomComparator(JSONCompareMode.LENIENT, new Customization("id", (o, t1) -> true))
        );
    }

    @SneakyThrows
    private String postProduct() {
        return mockMvc.perform(
                MockMvcRequestBuilders.post(
                        "/api/v1/products"
                )
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + createToken())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(TestProductFactory.newProduct()))
        ).andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }


    public String createToken() {
        long EXPIRATION_TIME = 864_000_000;

        KeyPair keyPair = RsaProvider.generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();

        return Jwts.builder()
                .setSubject(TestValues.CLIENT_ID)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .compact();
    }

    @SneakyThrows
    private String getResponse(String path) {
        return Files.asCharSource(
                ResourceUtils.getFile(path),
                Charset.defaultCharset()
        ).read();
    }

}
