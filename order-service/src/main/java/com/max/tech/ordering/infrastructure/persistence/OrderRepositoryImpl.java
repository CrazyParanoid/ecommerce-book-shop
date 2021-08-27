package com.max.tech.ordering.infrastructure.persistence;

import com.max.tech.ordering.domain.Order;
import com.max.tech.ordering.domain.OrderId;
import com.max.tech.ordering.domain.OrderRepository;
import com.max.tech.ordering.domain.person.PersonId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class OrderRepositoryImpl implements OrderRepository {
    private final OrderJPARepository orderJPARepository;

    public OrderRepositoryImpl(OrderJPARepository orderJPARepository) {
        this.orderJPARepository = orderJPARepository;
    }

    @Override
    public Optional<Order> findOrderById(OrderId orderId) {
        return orderJPARepository.findByOrderIdValue(orderId.getValue());
    }

    @Override
    public void save(Order order) {
        orderJPARepository.save(order);
    }

    @Override
    public List<Order> findPendingPaymentOrdersForClient(PersonId personId) {
        return orderJPARepository.findOrderByPersonIdAndStatus(personId, Order.Status.PENDING_PAYMENT);
    }

}
