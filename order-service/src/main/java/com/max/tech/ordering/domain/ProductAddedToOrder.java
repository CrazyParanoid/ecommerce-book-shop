package com.max.tech.ordering.domain;

import com.max.tech.ordering.domain.common.DomainEvent;
import com.max.tech.ordering.domain.product.ProductId;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
public class ProductAddedToOrder implements DomainEvent {
    private OrderId orderId;
    private ProductId productId;
    @Getter
    private Integer quantity;
    private Amount totalPrice;

    public String getOrderId(){
        return this.orderId.toString();
    }

    public String getProductId(){
        return this.productId.toString();
    }

    public BigDecimal getTotalPrice(){
        return this.totalPrice.getValue();
    }

}
