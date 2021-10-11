package com.max.tech.ordering.infrastructure.metrics;

import com.max.tech.ordering.domain.*;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class MetricsCollector {
    private final MeterRegistry meterRegistry;
    private final OrderRepository orderRepository;

    private final AtomicInteger pendingPaymentsOrderCounter = new AtomicInteger(0);
    private final AtomicInteger pendingCourierAssigmentOrderCounter = new AtomicInteger(0);
    private final AtomicInteger pendingForDeliveringOrderCounter = new AtomicInteger(0);

    private static final String PENDING_PAYMENT_ORDERS = "pending_payment_orders";
    private static final String PENDING_COURIER_ASSIGMENT_ORDERS = "pending_courier_assigment_orders";
    private static final String PENDING_FOR_DELIVERING_ORDERS = "pending_for_delivering_orders";

    public MetricsCollector(MeterRegistry meterRegistry, OrderRepository orderRepository) {
        this.meterRegistry = meterRegistry;
        this.orderRepository = orderRepository;
    }

    @PostConstruct
    public void init() {
        initGauge(pendingPaymentsOrderCounter, PENDING_PAYMENT_ORDERS, Order.Status.PENDING_PAYMENT);
        initGauge(pendingCourierAssigmentOrderCounter, PENDING_COURIER_ASSIGMENT_ORDERS, Order.Status.PENDING_COURIER_ASSIGMENT);
        initGauge(pendingForDeliveringOrderCounter, PENDING_FOR_DELIVERING_ORDERS, Order.Status.PENDING_FOR_DELIVERING);
    }

    private void initGauge(AtomicInteger orderCounter, String name, Order.Status status) {
        var orders = orderRepository.findOrdersByStatus(status);
        orderCounter.set(orders.size());
        meterRegistry.gauge(name, orderCounter);
    }

    @Async
    @TransactionalEventListener(classes = OrderPlaced.class)
    public void onOrderPlacedDomainEvent() {
        pendingPaymentsOrderCounter.incrementAndGet();
    }

    @Async
    @TransactionalEventListener(classes = OrderCourierAssigned.class)
    public void onOrderCourierAssignedDomainEvent() {
        pendingCourierAssigmentOrderCounter.decrementAndGet();
        pendingForDeliveringOrderCounter.incrementAndGet();
    }

    @Async
    @TransactionalEventListener(classes = OrderPaid.class)
    public void onOrderPaidDomainEvent() {
        pendingCourierAssigmentOrderCounter.incrementAndGet();
        pendingPaymentsOrderCounter.decrementAndGet();
    }

    @Async
    @TransactionalEventListener(classes = OrderDelivered.class)
    public void onOrderDeliveredDomainEvent() {
        pendingForDeliveringOrderCounter.decrementAndGet();
    }

}
