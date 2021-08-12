package com.max.tech.ordering.domain;

import com.max.tech.ordering.domain.common.DomainEvent;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class OrderCleaned implements DomainEvent {
    private OrderId orderId;

    public String getOrderId(){
        return this.orderId.toString();
    }
}
