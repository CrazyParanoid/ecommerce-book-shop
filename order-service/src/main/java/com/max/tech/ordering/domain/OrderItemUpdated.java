package com.max.tech.ordering.domain;

import com.max.tech.ordering.domain.common.DomainEvent;
import com.max.tech.ordering.domain.item.OrderItemId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
public class OrderItemUpdated implements DomainEvent {
    private OrderId orderId;
    private OrderItemId itemId;
    @Getter
    private Integer quantity;
    private Amount totalPrice;

    public String getOrderId() {return orderId.toString();}

    public String getItemId() {
        return itemId.toString();
    }

    public BigDecimal getTotalPrice() {
        return totalPrice.getValue();
    }

}
