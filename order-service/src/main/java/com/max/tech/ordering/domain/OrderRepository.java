package com.max.tech.ordering.domain;

import com.max.tech.ordering.domain.client.ClientId;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    Optional<Order> findOrderById(OrderId orderId);

    void save(Order order);

    List<Order> findPendingProductsOrdersForClient(ClientId clientId);

}
