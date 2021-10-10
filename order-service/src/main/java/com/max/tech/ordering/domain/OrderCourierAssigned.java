package com.max.tech.ordering.domain;

import com.max.tech.ordering.domain.common.DomainEvent;
import com.max.tech.ordering.domain.person.PersonId;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class OrderCourierAssigned implements DomainEvent {
    private OrderId orderId;
    private PersonId courierId;

    public String getOrderId() {
        return this.orderId.toString();
    }

    public String getCourierId() {
        return this.courierId.toString();
    }

}
