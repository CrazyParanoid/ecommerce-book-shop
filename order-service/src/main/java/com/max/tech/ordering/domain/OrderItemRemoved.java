package com.max.tech.ordering.domain;

import com.max.tech.ordering.domain.common.DomainEvent;
import com.max.tech.ordering.domain.item.OrderItemId;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
public class OrderItemRemoved implements DomainEvent {
    private OrderId orderId;
    private OrderItemId itemId;
    private Amount totalPrice;

    public String getOrderId(){
        return this.orderId.toString();
    }

    public String getItemId(){
        return this.itemId.toString();
    }

    public BigDecimal getTotalPrice(){
        return this.totalPrice.getValue();
    }

}
