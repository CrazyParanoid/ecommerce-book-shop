package com.max.tech.ordering.domain;

import com.max.tech.ordering.domain.common.DomainEvent;
import com.max.tech.ordering.domain.product.ProductId;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
public class OrderDelivered implements DomainEvent {
    private OrderId orderId;
    @Getter
    private LocalDateTime deliveredAt;
    private Map<ProductId, Integer> productsQuantities;

    public String getOrderId() {
        return this.orderId.toString();
    }

    public Map<UUID, Integer> getProductsQuantities() {
        return productsQuantities.entrySet()
                .stream()
                .collect(
                        Collectors.toMap(
                                e -> e.getKey().getValue(),
                                Map.Entry::getValue)
                );
    }

}
