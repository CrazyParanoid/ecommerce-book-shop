package com.max.tech.payment.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.max.tech.payment.Application;
import com.max.tech.payment.TestValues;
import com.max.tech.payment.config.TestAuthenticationConfig;
import com.max.tech.payment.core.Payment;
import com.max.tech.payment.core.PaymentGatewayAdapter;
import com.max.tech.payment.core.PaymentRepository;
import com.max.tech.payment.core.PaymentRequest;
import com.max.tech.payment.core.events.EventPublisher;
import com.max.tech.payment.web.WebUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebAppConfiguration
@ActiveProfiles({"security", "it"})
@EmbeddedKafka(
        partitions = 1,
        topics = "paymentEventsTopic"
)
@Import(TestAuthenticationConfig.class)
@SpringBootTest(classes = Application.class)
public class PaymentControllerE2ETest {
    @Autowired
    private EventPublisher eventPublisher;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private PaymentGatewayAdapter paymentGatewayAdapter;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    @SneakyThrows
    public void test_make_payment() {
        WebUtil.mockSecurity();
        Mockito.when(paymentGatewayAdapter.makePayment(ArgumentMatchers.any(PaymentRequest.class)))
                .thenReturn(TestValues.PAYMENT_ID);

        var payment = objectMapper.readValue(postPayment(), Payment.class);

        Assertions.assertEquals(payment.getId(), TestValues.PAYMENT_ID);
        Assertions.assertEquals(payment.getAmount(), TestValues.AMOUNT);
        Assertions.assertEquals(payment.getOrderId().toString(), TestValues.ORDER_ID);
        Assertions.assertEquals(payment.getClientId().toString(), TestValues.CLIENT_ID);
    }

    @SneakyThrows
    private String postPayment() {
        var request = new PaymentRequest(
                TestValues.ORDER_ID,
                TestValues.TOKEN,
                TestValues.AMOUNT,
                TestValues.CLIENT_ID);

        return mockMvc.perform(
                MockMvcRequestBuilders.post(
                        "/api/v1/payments"
                )
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + WebUtil.createToken())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request))
        ).andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

}
