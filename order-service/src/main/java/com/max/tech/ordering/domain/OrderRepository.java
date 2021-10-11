package com.max.tech.ordering.domain;

import com.max.tech.ordering.domain.person.PersonId;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, OrderId> {

    @EntityGraph(value = "order-graph", type = EntityGraph.EntityGraphType.LOAD)
    @Query(value = "select o from Order o where o.orderId = :orderId")
    Optional<Order> findOrderById(@Param("orderId") OrderId orderId);

    @EntityGraph(value = "order-graph", type = EntityGraph.EntityGraphType.LOAD)
    @Query(value = "select o from Order o where o.personId = :personId and o.status = 'PENDING_PAYMENT'")
    List<Order> findPendingPaymentOrdersForClient(@Param("personId") PersonId personId);

    @EntityGraph(value = "order-graph", type = EntityGraph.EntityGraphType.LOAD)
    List<Order> findOrdersByStatus(Order.Status status);

}
