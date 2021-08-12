package com.max.tech.ordering.e2e;

import com.max.tech.ordering.Application;
import com.max.tech.ordering.config.TestAuthenticationConfig;
import com.max.tech.ordering.domain.client.ClientId;
import com.max.tech.ordering.domain.client.ClientRepository;
import com.max.tech.ordering.infrastructure.events.subscribers.client.Address;
import com.max.tech.ordering.infrastructure.events.subscribers.client.ClientRegistered;
import com.max.tech.ordering.util.TestValues;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@ActiveProfiles({"security", "it"})
@EmbeddedKafka(
        partitions = 1,
        topics = "clientEventsTopic"
)
@Import(TestAuthenticationConfig.class)
@SpringBootTest(classes = Application.class)
public class E2ERegisterNewClientTest {
    private static final String EVENT_TYPE_VALUE = "ClientRegistered";
    private static final String EVENT_TYPE = "type";

    @Autowired
    @Qualifier("clientChannel")
    private MessageChannel inputMessageChannel;
    @Autowired
    private ClientRepository clientRepository;

    @Test
    public void test_register_new_client() {
        inputMessageChannel.send(MessageBuilder.withPayload(newClientRegisteredEvent())
                .setHeader(EVENT_TYPE, EVENT_TYPE_VALUE)
                .build());

        Awaitility.await()
                .atMost(Duration.ofSeconds(6))
                .pollDelay(3L, TimeUnit.SECONDS)
                .untilAsserted(
                        () -> {
                            var client = clientRepository.findClientById(ClientId.fromValue(TestValues.CLIENT_ID));
                            Assertions.assertTrue(client.isPresent());
                        }
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
