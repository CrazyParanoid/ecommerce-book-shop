package com.max.tech.payment.core.events.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoredEventRepository extends JpaRepository<StoredEvent, Long> {

    Optional<StoredEvent> getFirstByIdNotNull();

}
