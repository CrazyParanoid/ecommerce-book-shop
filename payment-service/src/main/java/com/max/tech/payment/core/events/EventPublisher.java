package com.max.tech.payment.core.events;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.max.tech.payment.core.events.persistence.HibernateProxyTypeAdapter;
import com.max.tech.payment.core.events.persistence.StoredEvent;
import com.max.tech.payment.core.events.persistence.StoredEventRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class EventPublisher {
    private final OutputBindings outputBindings;
    private final StoredEventRepository storedEventRepository;

    private static final String EVENT_TYPE_HEADER = "type";

    @Autowired
    public EventPublisher(OutputBindings outputBindings, StoredEventRepository storedEventRepository) {
        this.outputBindings = outputBindings;
        this.storedEventRepository = storedEventRepository;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public <T extends Event> void publish(T event) {
        var gson = new GsonBuilder().registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY).create();
        try {
            outputBindings.paymentChannel()
                    .send(MessageBuilder.withPayload(event)
                            .setHeader(EVENT_TYPE_HEADER, event.getClass().getSimpleName())
                            .build());
        } catch (Exception ex) {
            storedEventRepository.save(new StoredEvent(gson.toJson(event), event.getClass().getName()));
        }
    }

    @SneakyThrows
    @Scheduled(fixedDelayString = "${events.delay}")
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void findAndPublishDomainEvent() {
        var optionalStoredDomainEvent = storedEventRepository.getFirstByIdNotNull();

        if (optionalStoredDomainEvent.isPresent()) {

            var storedDomainEvent = optionalStoredDomainEvent.get();
            var clazz = Class.forName(storedDomainEvent.getType());

            var domainEvent = new Gson().fromJson(
                    storedDomainEvent.getPayload(),
                    clazz
            );

            publishDomainEvent(clazz.getSimpleName(), domainEvent);
            storedEventRepository.delete(storedDomainEvent);
        }
    }

    private <T> void publishDomainEvent(String eventType, T payload) {
        outputBindings.paymentChannel()
                .send(
                        MessageBuilder.withPayload(payload)
                                .setHeader(EVENT_TYPE_HEADER, eventType)
                                .build()
                );
        log.info("{} domain event has been published", eventType);
    }

}
