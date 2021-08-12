package com.max.tech.ordering.infrastructure.events;

import com.max.tech.ordering.Application;
import com.max.tech.ordering.infrastructure.events.subscribers.client.Address;
import com.max.tech.ordering.util.TestValues;
import com.max.tech.ordering.application.client.ClientService;
import com.max.tech.ordering.application.client.dto.RegisterNewClientCommand;
import com.max.tech.ordering.infrastructure.events.subscribers.client.ClientRegistered;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
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
        topics = "clientEventsTopic"
)
@SpringBootTest(classes = Application.class)
public class ClientRegisteredEventSubscriberIT {
    @Autowired
    @Qualifier("clientChannel")
    private MessageChannel inputMessageChannel;
    @MockBean
    private ClientService clientService;

    private static final String EVENT_TYPE_HEADER = "type";
    private static final String EVENT_TYPE_VALUE = "ClientRegistered";

    @Test
    public void test_on_client_registered_event() {
        var event = newClientRegisteredEvent();

        inputMessageChannel.send(
                MessageBuilder.withPayload(event)
                        .setHeader(EVENT_TYPE_HEADER, EVENT_TYPE_VALUE)
                        .build()
        );

        Awaitility.await()
                .atMost(Duration.ofSeconds(6))
                .pollDelay(3L, TimeUnit.SECONDS)
                .untilAsserted(
                        () -> Mockito.verify(clientService, Mockito.times(1))
                                .registerNewClient(
                                        ArgumentMatchers.any(RegisterNewClientCommand.class)
                                )
                );
    }

    private ClientRegistered newClientRegisteredEvent() {
        return new ClientRegistered(
                TestValues.CLIENT_ID,
                new Address(
                        TestValues.CITY,
                        TestValues.STREET,
                        TestValues.HOUSE,
                        TestValues.FLAT,
                        TestValues.FLOOR,
                        TestValues.ENTRANCE
                )
        );
    }
}
