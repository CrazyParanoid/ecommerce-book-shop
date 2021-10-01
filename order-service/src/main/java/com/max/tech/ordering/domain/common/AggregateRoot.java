package com.max.tech.ordering.domain.common;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.AbstractAggregateRoot;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import java.util.List;

@MappedSuperclass
public abstract class AggregateRoot extends AbstractAggregateRoot<AggregateRoot> implements Entity {
    @CreatedDate
    @Column(name = "created_at", columnDefinition = "DATE")
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Column(name = "updated_at", columnDefinition = "DATE")
    private LocalDateTime updatedAt;

    protected <T extends DomainEvent> void raiseDomainEvent(T domainEvent) {
        registerEvent(domainEvent);
    }

    public List<? super DomainEvent> getDomainEvents() {
        return (List<? super DomainEvent>) domainEvents();
    }

}
