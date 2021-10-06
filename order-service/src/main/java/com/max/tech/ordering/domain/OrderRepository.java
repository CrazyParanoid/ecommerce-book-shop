package com.max.tech.ordering.domain;

import com.max.tech.ordering.domain.person.PersonId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, OrderId> {

    @Query(value = "select o from Order o where o.orderId = :orderId")
    Optional<Order> findOrderById(@Param("orderId") OrderId orderId);

    @Query(value = "select o from Order o where o.personId = :personId and o.status = 'PENDING_PAYMENT'")
    List<Order> findPendingPaymentOrdersForClient(@Param("personId") PersonId personId);

}
