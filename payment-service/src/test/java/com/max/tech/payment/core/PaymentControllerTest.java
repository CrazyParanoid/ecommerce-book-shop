package com.max.tech.payment.core;

import com.max.tech.payment.TestValues;
import com.max.tech.payment.core.events.EventPublisher;
import com.max.tech.payment.core.events.PaymentDone;
import com.max.tech.payment.web.PaymentController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

public class PaymentControllerTest {
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private EventPublisher eventPublisher;
    @Mock
    private PaymentGatewayAdapter paymentGatewayAdapter;

    @Captor
    private ArgumentCaptor<Payment> paymentArgumentCaptor;
    @Captor
    private ArgumentCaptor<PaymentDone> paymentDoneArgumentCaptor;

    @InjectMocks
    private PaymentController paymentController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void test_make_payment() {
        Mockito.when(paymentRepository.save(paymentArgumentCaptor.capture()))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        Mockito.doNothing().when(eventPublisher).publish(paymentDoneArgumentCaptor.capture());
        Mockito.when(paymentGatewayAdapter.makePayment(ArgumentMatchers.any(PaymentRequest.class)))
                .thenReturn(TestValues.PAYMENT_ID);

        paymentController.postPayment(new PaymentRequest(
                TestValues.ORDER_ID,
                TestValues.TOKEN,
                TestValues.AMOUNT,
                TestValues.CLIENT_ID
        ));

        assertPayment(paymentArgumentCaptor.getValue());
        assertPaymentDone(paymentDoneArgumentCaptor.getValue());
    }

    private void assertPayment(Payment payment) {
        Assertions.assertEquals(payment.getOrderId().toString(), TestValues.ORDER_ID);
        Assertions.assertEquals(payment.getClientId().toString(), TestValues.CLIENT_ID);
        Assertions.assertEquals(payment.getAmount(), TestValues.AMOUNT);
        Assertions.assertEquals(payment.getId(), TestValues.PAYMENT_ID);
    }

    private void assertPaymentDone(PaymentDone paymentDone) {
        Assertions.assertEquals(paymentDone.getPaymentId(), TestValues.PAYMENT_ID);
        Assertions.assertEquals(paymentDone.getOrderId(), TestValues.ORDER_ID);
    }

}

