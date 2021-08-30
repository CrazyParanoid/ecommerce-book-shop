package com.max.tech.ordering.domain;

import com.max.tech.ordering.domain.person.PersonId;
import com.max.tech.ordering.domain.common.DomainEvent;
import com.max.tech.ordering.domain.item.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@AllArgsConstructor
public class OrderPlaced implements DomainEvent {
    private OrderId orderId;
    private PersonId personId;
    private AddressId deliveryAddressId;
    @Getter
    private Set<OrderItem> orderItems;

    public String getClientId() {
        return this.personId.toString();
    }

    public String getOrderId() {
        return this.orderId.toString();
    }

    public String getDeliveryAddressId() {
        return deliveryAddressId.toString();
    }
}
