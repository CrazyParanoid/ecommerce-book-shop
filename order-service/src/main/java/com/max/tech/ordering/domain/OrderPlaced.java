package com.max.tech.ordering.domain;

import com.max.tech.ordering.domain.person.PersonId;
import com.max.tech.ordering.domain.common.DomainEvent;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class OrderPlaced implements DomainEvent {
    private OrderId orderId;
    private PersonId personId;
    private AddressId deliveryAddressId;

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
