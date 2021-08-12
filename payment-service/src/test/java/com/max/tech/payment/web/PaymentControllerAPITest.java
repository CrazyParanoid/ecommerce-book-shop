package com.max.tech.payment.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.max.tech.payment.TestValues;
import com.max.tech.payment.config.TestAuthenticationConfig;
import com.max.tech.payment.core.Payment;
import com.max.tech.payment.core.PaymentGatewayAdapter;
import com.max.tech.payment.core.PaymentRepository;
import com.max.tech.payment.core.PaymentRequest;
import com.max.tech.payment.core.events.EventPublisher;
import com.max.tech.payment.core.events.PaymentDone;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ActiveProfiles("security")
@Import(TestAuthenticationConfig.class)
@WebMvcTest(value = PaymentController.class)
public class PaymentControllerAPITest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PaymentRepository paymentRepository;
    @MockBean
    private PaymentGatewayAdapter paymentGatewayAdapter;
    @MockBean
    private EventPublisher eventPublisher;

    @Test
    @SneakyThrows
    public void test_post_payment() {
        WebUtil.mockSecurity();
        Mockito.when(paymentRepository.save(ArgumentMatchers.any(Payment.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        Mockito.doNothing().when(eventPublisher).publish(ArgumentMatchers.any(PaymentDone.class));
        Mockito.when(paymentGatewayAdapter.makePayment(ArgumentMatchers.any(PaymentRequest.class)))
                .thenReturn(TestValues.PAYMENT_ID);

        var actualResponse = postPayment();

        var expectedResponse = WebUtil.getResponse("classpath:json/payment_response.json");
        JSONAssert.assertEquals(actualResponse, expectedResponse, JSONCompareMode.LENIENT);
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
