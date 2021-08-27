package com.max.tech.ordering.infrastructure.events;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoredDomainEventRepository extends JpaRepository<StoredDomainEvent, Long> {

    Optional<StoredDomainEvent> getFirstByIdNotNull();

}
