package com.max.tech.ordering.infrastructure.persistence;

import com.max.tech.ordering.domain.Order;
import com.max.tech.ordering.domain.OrderId;
import com.max.tech.ordering.domain.person.PersonId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderJPARepository extends JpaRepository<Order, OrderId> {

    Optional<Order> findByOrderIdValue(UUID value);

    List<Order> findOrderByPersonIdAndStatus(PersonId personId, Order.Status status);

}
