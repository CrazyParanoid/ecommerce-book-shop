package com.max.tech.ordering.domain.common;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

@MappedSuperclass
public abstract class AggregateRoot extends Auditable implements Entity {
    @Transient
    private final List<? super DomainEvent> domainEvents = new ArrayList<>();

    protected <T extends DomainEvent> void raiseDomainEvent(T domainEvent) {
        this.domainEvents.add(domainEvent);
    }

    public List<? super DomainEvent> getDomainEvents() {
        return domainEvents;
    }

}
