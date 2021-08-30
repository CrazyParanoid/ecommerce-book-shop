package com.max.tech.ordering.infrastructure.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.max.tech.ordering.Application;
import com.max.tech.ordering.domain.TestDomainObjectsFactory;
import com.max.tech.ordering.domain.common.DomainEventPublisher;
import com.max.tech.ordering.config.TransactionTemplateConfig;
import com.max.tech.ordering.helper.TestValues;
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

import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@ActiveProfiles("it")
@EmbeddedKafka(
        partitions = 1,
        topics = "orderEventsTopic"
)
@Import(TransactionTemplateConfig.class)
@SpringBootTest(classes = Application.class)
public class DomainEventPublisherIT {
    @Autowired
    private DomainEventPublisher domainEventPublisher;
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
    public void shouldPublishOrderPlacedDomainEvent() {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                domainEventPublisher.publish(Collections.singletonList(
                        TestDomainObjectsFactory.raiseOrderPlacedDomainEvent())
                );
            }
        });

        var payload = Objects.requireNonNull(
                messageCollector.forChannel(outputBindings.orderChannel())
                        .poll(6, TimeUnit.SECONDS)
        ).getPayload();

        var integrationEvent = objectMapper.readValue((String) payload, OrderPlacedIntegrationEvent.class);
        Assertions.assertNotNull(integrationEvent);
        Assertions.assertEquals(integrationEvent.getOrderId(), TestValues.ORDER_ID);
        Assertions.assertEquals(integrationEvent.getClientId(), TestValues.CLIENT_ID);
        Assertions.assertEquals(integrationEvent.getDeliveryAddressId(), TestValues.ADDRESS_ID);
    }

}
