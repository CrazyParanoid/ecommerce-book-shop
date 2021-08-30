package com.max.tech.ordering.domain;

import com.max.tech.ordering.domain.common.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
public class OrderDelivered implements DomainEvent {
    private OrderId orderId;
    @Getter
    private LocalDateTime deliveredAt;

    public String getOrderId() {
        return this.orderId.toString();
    }

}
