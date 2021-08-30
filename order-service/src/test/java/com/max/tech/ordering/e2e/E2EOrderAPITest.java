package com.max.tech.ordering.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.max.tech.ordering.Application;
import com.max.tech.ordering.application.TestApplicationObjectsFactory;
import com.max.tech.ordering.application.dto.OrderDTO;
import com.max.tech.ordering.config.TestAuthenticationConfig;
import com.max.tech.ordering.domain.Order;
import com.max.tech.ordering.helper.TestValues;
import com.max.tech.ordering.helper.WebUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
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
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    @SneakyThrows
    public void shouldPlaceOrder() {
        WebUtil.mockSecurity();

        var response = postNewOrder();

        var order = objectMapper.readValue(response, OrderDTO.class);
        assertOrderDTO(order);
    }

    private void assertOrderDTO(OrderDTO orderDTO) {
        Assertions.assertNotNull(orderDTO.getOrderId());
        Assertions.assertEquals(orderDTO.getClientId(), TestValues.CLIENT_ID);
        Assertions.assertNull(orderDTO.getDeliveredAt());
        Assertions.assertEquals(orderDTO.getStatus(), Order.Status.PENDING_PAYMENT.name());
        Assertions.assertNull(orderDTO.getCourierId());
        Assertions.assertEquals(orderDTO.getTotalPrice(), TestValues.TOTAL_ORDER_PRICE_WITH_ONE_ITEM);
        Assertions.assertFalse(orderDTO.getItems().isEmpty());
        Assertions.assertNull(orderDTO.getPaymentId());
        Assertions.assertEquals(orderDTO.getDeliveryAddressId(), TestValues.ADDRESS_ID);

        var itemDTO = orderDTO.getItems()
                .stream()
                .findFirst()
                .orElse(null);

        Assertions.assertNotNull(itemDTO);
        Assertions.assertEquals(itemDTO.getItemId(), TestValues.FIRST_ITEM_ID);
        Assertions.assertEquals(itemDTO.getPrice(), TestValues.FIRST_ITEM_PRICE);
        Assertions.assertEquals(itemDTO.getQuantity(), TestValues.FIRST_ITEM_QUANTITY);
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
                                        TestApplicationObjectsFactory.newPlaceOrderCommand())
                                )
                ).andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

}
