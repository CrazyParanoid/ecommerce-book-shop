package com.max.tech.ordering.infrastructure.events;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.max.tech.ordering.domain.common.DomainEvent;
import com.max.tech.ordering.domain.common.DomainEventPublisher;
import com.max.tech.ordering.domain.item.OrderItem;
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

    private static final String EVENT_TYPE_HEADER = "type";

    @Autowired
    public DomainEventPublisherImpl(StoredDomainEventRepository storedDomainEventRepository,
                                    OutputBindings outputBindings) {
        this.storedDomainEventRepository = storedDomainEventRepository;
        this.outputBindings = outputBindings;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void publish(List<? super DomainEvent> domainEvents) {
        var gson = new GsonBuilder().registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY)
                .addSerializationExclusionStrategy(
                        new ExclusionStrategy() {
                            @Override
                            public boolean shouldSkipField(FieldAttributes field) {
                                return field.getDeclaringClass() == OrderItem.class
                                        && field.getName().equals("order");
                            }

                            @Override
                            public boolean shouldSkipClass(Class<?> aClass) {
                                return false;
                            }
                        }
                ).create();

        domainEvents.forEach(e -> {
                    try {
                        outputBindings.orderChannel()
                                .send(MessageBuilder.withPayload(e)
                                        .setHeader(EVENT_TYPE_HEADER, e.getClass().getSimpleName())
                                        .build());
                        publishDomainEvent(e.getClass().getSimpleName(), e);
                    } catch (Exception ex) {
                        storedDomainEventRepository.save(new StoredDomainEvent(gson.toJson(e), e.getClass().getName()));
                    }
                }
        );
    }

    @SneakyThrows
    @Scheduled(fixedDelayString = "${events.delay}")
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void findAndPublishDomainEvent() {
        var optionalStoredDomainEvent = storedDomainEventRepository.getFirstByIdNotNull();

        if (optionalStoredDomainEvent.isPresent()) {

            var storedDomainEvent = optionalStoredDomainEvent.get();
            var clazz = Class.forName(storedDomainEvent.getType());

            var domainEvent = new Gson().fromJson(
                    storedDomainEvent.getPayload(),
                    clazz
            );

            publishDomainEvent(clazz.getSimpleName(), domainEvent);
            storedDomainEventRepository.delete(storedDomainEvent);
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
