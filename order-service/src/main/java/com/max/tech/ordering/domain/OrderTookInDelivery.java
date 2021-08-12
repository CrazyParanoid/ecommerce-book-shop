package com.max.tech.ordering.domain;

import com.max.tech.ordering.domain.common.DomainEvent;
import com.max.tech.ordering.domain.employee.EmployeeId;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class OrderTookInDelivery implements DomainEvent {
    private OrderId orderId;
    private EmployeeId courierId;

    public String getOrderId() {
        return this.orderId.toString();
    }

    public String getCourierId() {
        return this.courierId.toString();
    }

}
