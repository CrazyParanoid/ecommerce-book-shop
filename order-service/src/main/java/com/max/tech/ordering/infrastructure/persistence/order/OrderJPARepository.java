package com.max.tech.ordering.infrastructure.persistence.order;

import com.max.tech.ordering.domain.Order;
import com.max.tech.ordering.domain.OrderId;
import com.max.tech.ordering.domain.client.ClientId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderJPARepository extends JpaRepository<Order, OrderId> {

    Optional<Order> findByOrderIdValue(UUID value);

    List<Order> findOrderByClientIdAndStatus(ClientId clientId, Order.Status status);

}
