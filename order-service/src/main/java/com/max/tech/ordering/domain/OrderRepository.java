package com.max.tech.ordering.domain;

import com.max.tech.ordering.domain.person.PersonId;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    Optional<Order> findOrderById(OrderId orderId);

    void save(Order order);

    List<Order> findPendingPaymentOrdersForClient(PersonId personId);

}
