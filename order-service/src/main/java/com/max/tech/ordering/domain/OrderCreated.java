package com.max.tech.ordering.domain;

import com.max.tech.ordering.domain.client.ClientId;
import com.max.tech.ordering.domain.common.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class OrderCreated implements DomainEvent {
    private OrderId orderId;
    private ClientId clientId;
    @Getter
    private Address deliveryAddress;

    public String getClientId() {
        return this.clientId.toString();
    }

    public String getOrderId() {
        return this.orderId.toString();
    }

}
