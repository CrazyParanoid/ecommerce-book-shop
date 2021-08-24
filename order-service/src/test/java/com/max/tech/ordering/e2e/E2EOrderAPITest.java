package com.max.tech.ordering.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.max.tech.ordering.Application;
import com.max.tech.ordering.application.TestApplicationObjectsFactory;
import com.max.tech.ordering.application.order.dto.OrderDTO;
import com.max.tech.ordering.config.TestAuthenticationConfig;
import com.max.tech.ordering.domain.TestDomainObjectsFactory;
import com.max.tech.ordering.domain.client.ClientRepository;
import com.max.tech.ordering.util.AssertionUtil;
import com.max.tech.ordering.util.WebUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


@ActiveProfiles({"security", "it"})
@EmbeddedKafka(
        partitions = 1,
        topics = "orderEventsTopic"
)
@Import(TestAuthenticationConfig.class)
@WebAppConfiguration
@SpringBootTest(classes = Application.class)
public class E2EOrderAPITest {
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    @SneakyThrows
    public void test_create_new_order() {
        WebUtil.mockSecurity();
        clientRepository.save(TestDomainObjectsFactory.newClient());

        var response = postNewOrder();

        var order = objectMapper.readValue(response, OrderDTO.class);
        AssertionUtil.assertOrderDTO(order);
    }

    @SneakyThrows
    private String postNewOrder() {
        return mockMvc.perform(
                MockMvcRequestBuilders.post(
                        "/api/v1/orders"
                )
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + WebUtil.createToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                TestApplicationObjectsFactory.newCreateNewOrderCommand())
                        )
        ).andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

}
