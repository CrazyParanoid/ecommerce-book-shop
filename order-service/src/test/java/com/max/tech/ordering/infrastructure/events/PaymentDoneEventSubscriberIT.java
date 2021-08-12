package com.max.tech.ordering.infrastructure.events;

import com.max.tech.ordering.Application;
import com.max.tech.ordering.application.order.OrderService;
import com.max.tech.ordering.infrastructure.events.subscribers.payment.PaymentDone;
import com.max.tech.ordering.util.TestValues;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@ActiveProfiles("it")
@EmbeddedKafka(
        partitions = 1,
        topics = "paymentEventsTopic"
)
@SpringBootTest(classes = Application.class)
public class PaymentDoneEventSubscriberIT {
    @Autowired
    @Qualifier("paymentChannel")
    private MessageChannel inputMessageChannel;
    @MockBean
    private OrderService orderService;

    private static final String EVENT_TYPE_HEADER = "type";
    private static final String EVENT_TYPE_VALUE = "PaymentDone";

    @Test
    public void test_on_payment_done_event() {
        inputMessageChannel.send(
                MessageBuilder.withPayload(
                        new PaymentDone(TestValues.ORDER_ID, TestValues.PAYMENT_ID)
                )
                        .setHeader(EVENT_TYPE_HEADER, EVENT_TYPE_VALUE)
                        .build()
        );

        Awaitility.await()
                .atMost(Duration.ofSeconds(6))
                .pollDelay(3L, TimeUnit.SECONDS)
                .untilAsserted(
                        () -> Mockito.verify(orderService, Mockito.times(1))
                                .confirmOrderPayment(TestValues.ORDER_ID, TestValues.PAYMENT_ID)
                );
    }

}
