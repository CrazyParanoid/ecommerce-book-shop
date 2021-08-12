package com.max.tech.payment.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.max.tech.payment.Application;
import com.max.tech.payment.TestValues;
import com.max.tech.payment.config.TransactionTemplateConfig;
import com.max.tech.payment.core.events.EventPublisher;
import com.max.tech.payment.core.events.OutputBindings;
import com.max.tech.payment.core.events.PaymentDone;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@ActiveProfiles("it")
@EmbeddedKafka(
        partitions = 1,
        topics = "paymentEventsTopic"
)
@Import(TransactionTemplateConfig.class)
@SpringBootTest(classes = Application.class)
public class EventPublisherIT {
    @Autowired
    private EventPublisher eventPublisher;
    @Autowired
    private MessageCollector messageCollector;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private OutputBindings outputBindings;
    @Autowired
    private TransactionTemplate transactionTemplate;

    @Test
    @SneakyThrows
    public void test_publish_order_created_event() {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                eventPublisher.publish(new PaymentDone(
                        TestValues.ORDER_ID,
                        TestValues.PAYMENT_ID
                ));
            }
        });

        var payload = Objects.requireNonNull(
                messageCollector.forChannel(outputBindings.paymentChannel())
                        .poll(6, TimeUnit.SECONDS)
        ).getPayload();

        var event = objectMapper.readValue((String) payload, PaymentDone.class);
        Assertions.assertEquals(event.getOrderId(), TestValues.ORDER_ID);
        Assertions.assertEquals(event.getPaymentId(), TestValues.PAYMENT_ID);
    }
}
