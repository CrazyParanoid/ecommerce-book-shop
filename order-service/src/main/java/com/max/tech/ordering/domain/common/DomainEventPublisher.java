package com.max.tech.ordering.domain.common;

import java.util.List;

public interface DomainEventPublisher {

    void publish(List<? super DomainEvent> domainEvents);

}
