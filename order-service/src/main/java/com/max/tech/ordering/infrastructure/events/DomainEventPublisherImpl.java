package com.max.tech.ordering.infrastructure.events;

import brave.Tracer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.max.tech.ordering.domain.common.DomainEvent;
import com.max.tech.ordering.domain.common.DomainEventPublisher;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class DomainEventPublisherImpl implements DomainEventPublisher {
    private final StoredDomainEventRepository storedDomainEventRepository;
    private final OutputBindings outputBindings;
    private final ObjectMapper objectMapper;
    private final Tracer tracer;

    private static final String EVENT_TYPE_HEADER = "type";

    @Autowired
    public DomainEventPublisherImpl(StoredDomainEventRepository storedDomainEventRepository,
                                    OutputBindings outputBindings, ObjectMapper objectMapper,
                                    Tracer tracer) {
        this.storedDomainEventRepository = storedDomainEventRepository;
        this.outputBindings = outputBindings;
        this.objectMapper = objectMapper;
        this.tracer = tracer;
    }

    @Override
    @SneakyThrows
    @Transactional(propagation = Propagation.MANDATORY)
    public void publish(List<? super DomainEvent> domainEvents) {
        for (Object event : domainEvents) {
            try {
                outputBindings.orderChannel()
                        .send(MessageBuilder.withPayload(event)
                                .setHeader(EVENT_TYPE_HEADER, event.getClass().getSimpleName())
                                .build());
                publishDomainEvent(event.getClass().getSimpleName(), event);

            } catch (Exception ex) {
                log.error("Error during publishing domain event {}: {}", event.getClass().getSimpleName(), ex.getMessage());

                storedDomainEventRepository.save(new StoredDomainEvent(
                        objectMapper.writeValueAsString(event),
                        event.getClass().getName()));
            }
        }
    }

    @SneakyThrows
    @Scheduled(fixedDelayString = "${events.delay}")
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void findAndPublishDomainEvent() {
        var optionalStoredDomainEvent = storedDomainEventRepository.getFirstByIdNotNull();

        if (optionalStoredDomainEvent.isPresent()) {
            var storedDomainEvent = optionalStoredDomainEvent.get();
            var clazz = Class.forName(storedDomainEvent.getType());

            var domainEvent = objectMapper.readValue(storedDomainEvent.getPayload(), Object.class);

            publishDomainEvent(clazz.getSimpleName(), domainEvent);
            storedDomainEventRepository.delete(storedDomainEvent);
            sendSpan();
        }
    }

    private void sendSpan() {
        var span = tracer.nextSpan().name("Order span").start();
        try (var ignored = tracer.withSpanInScope(span.start())) {
            log.debug("Span with id {} has been sent", span.context().spanId());
        } finally {
            span.finish();
        }
    }

    private <T> void publishDomainEvent(String eventType, T payload) {
        outputBindings.orderChannel()
                .send(
                        MessageBuilder.withPayload(payload)
                                .setHeader(EVENT_TYPE_HEADER, eventType)
                                .build()
                );
        log.info("{} domain event has been published", eventType);
    }

}
