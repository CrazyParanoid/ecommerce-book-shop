package com.max.tech.ordering.domain;

import com.max.tech.ordering.domain.common.DomainEvent;
import com.max.tech.ordering.domain.payment.PaymentId;
import com.max.tech.ordering.domain.item.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.Set;

@Data
@AllArgsConstructor
public class OrderPaid implements DomainEvent {
    private OrderId orderId;
    private PaymentId paymentId;
    @Getter
    private Set<OrderItem> orderItems;

    public String getOrderId() {
        return this.orderId.toString();
    }

    public String getPaymentId() {return paymentId.toString();}

}
