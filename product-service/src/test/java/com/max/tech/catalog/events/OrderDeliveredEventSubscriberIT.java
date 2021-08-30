package com.max.tech.catalog.events;

import com.max.tech.catalog.TestProductFactory;
import com.max.tech.catalog.TestValues;
import com.max.tech.catalog.catalog.Application;
import com.max.tech.catalog.catalog.events.order.OrderDelivered;
import com.max.tech.catalog.catalog.product.Product;
import com.max.tech.catalog.catalog.product.ProductRepository;
import com.max.tech.catalog.config.MongoConfig;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@ActiveProfiles("it")
@EmbeddedKafka(
        partitions = 1,
        topics = "orderEventsTopic"
)
@Import(MongoConfig.class)
@SpringBootTest(classes = Application.class)
public class OrderDeliveredEventSubscriberIT {
    @Autowired
    @Qualifier("orderChannel")
    private MessageChannel inputMessageChannel;
    @MockBean
    private ProductRepository productRepository;

    private static final String EVENT_TYPE_HEADER = "type";
    private static final String EVENT_TYPE_VALUE = "OrderDelivered";

    @Test
    public void shouldGetOrderDeliveredEvent() {
        Mockito.when(productRepository.findById(ArgumentMatchers.any(UUID.class)))
                .thenReturn(Optional.of(TestProductFactory.newProduct()));
        Mockito.when(productRepository.save(ArgumentMatchers.any(Product.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        inputMessageChannel.send(
                MessageBuilder.withPayload(
                        new OrderDelivered(Map.of(TestValues.FIRST_PRODUCT_ID, TestValues.FIRST_PRODUCT_QUANTITY))
                )
                        .setHeader(EVENT_TYPE_HEADER, EVENT_TYPE_VALUE)
                        .build()
        );

        Awaitility.await()
                .atMost(Duration.ofSeconds(6))
                .pollDelay(3L, TimeUnit.SECONDS)
                .untilAsserted(
                        () -> Mockito.verify(productRepository, Mockito.times(1))
                                .save(ArgumentMatchers.any(Product.class))
                );
    }
}
