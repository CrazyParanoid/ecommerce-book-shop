package com.max.tech.ordering.infrastructure.metrics;

import com.max.tech.ordering.domain.*;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.annotation.PostConstruct;

@Component
public class MetricsCollector {
    private final MeterRegistry meterRegistry;

    public MetricsCollector(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @PostConstruct
    public void init() {
        meterRegistry.counter("orders", "status", "pending_payment");
        meterRegistry.counter("orders", "status", "pending_courier_assigment");
        meterRegistry.counter("orders", "status", "pending_for_delivering");
    }

    @Async
    @TransactionalEventListener(classes = OrderPlaced.class)
    public void onOrderPlacedDomainEvent() {
        var pendingPaymentOrdersCounter = meterRegistry.get("orders")
                .tags("status", "pending_payment")
                .counter();
        pendingPaymentOrdersCounter.increment();
    }

    @Async
    @TransactionalEventListener(classes = OrderCourierAssigned.class)
    public void onOrderCourierAssignedDomainEvent() {
        var pendingCourierAssigmentCounter = meterRegistry.get("orders")
                .tags("status", "pending_courier_assigment")
                .counter();
        var pendingForDeliveryCounter = meterRegistry.get("orders")
                .tags("status", "pending_for_delivery")
                .counter();

        pendingCourierAssigmentCounter.increment(-1);
        pendingForDeliveryCounter.increment();
    }

    @Async
    @TransactionalEventListener(classes = OrderPaid.class)
    public void onOrderPaidDomainEvent(){
        var pendingCourierAssigmentCounter = meterRegistry.get("orders")
                .tags("status", "pending_courier_assigment")
                .counter();
        var pendingPaymentOrdersCounter = meterRegistry.get("orders")
                .tags("status", "pending_payment")
                .counter();

        pendingCourierAssigmentCounter.increment();
        pendingPaymentOrdersCounter.increment(-1);
    }

    @Async
    @TransactionalEventListener(classes = OrderDelivered.class)
    public void onOrderDeliveredDomainEvent() {
        var pendingForDeliveryCounter = meterRegistry.get("orders")
                .tags("status", "pending_for_delivery")
                .counter();

        pendingForDeliveryCounter.increment(-1);
    }

}
