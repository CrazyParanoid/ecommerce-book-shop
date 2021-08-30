package com.max.tech.ordering.domain.item;

import com.max.tech.ordering.domain.Amount;
import com.max.tech.ordering.domain.Order;
import com.max.tech.ordering.domain.common.Entity;
import com.max.tech.ordering.domain.common.IdentifiedDomainObject;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@AllArgsConstructor
@Table(name = "items")
@javax.persistence.Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem extends IdentifiedDomainObject implements Entity {
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "item_id"))
    private OrderItemId itemId;
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "price"))
    private Amount price;
    @Getter
    private Integer quantity;
    @Setter
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order = null;

    public Amount totalPrice() {
        return this.price.multiply(
                Double.valueOf(this.quantity)
        );
    }

    public void validate() {
        if (this.quantity <= 0)
            throw new IllegalArgumentException(String.format("Illegal quantity %d. " +
                    "Quantity must be greater than 0", this.quantity));
        if (this.price.isNegativeOrZero())
            throw new IllegalArgumentException(String.format("Illegal price %s. Price must be greater than 0",
                    this.price.toString()));
    }

    public void update(Integer quantity) {
        this.quantity = quantity;
        validate();
    }

    public Amount price() {
        return this.price;
    }

    public OrderItemId itemId() {
        return this.itemId;
    }

    public Integer quantity() {
        return this.quantity;
    }

    public String getItemId() {
        return itemId.toString();
    }

    public BigDecimal getPrice() {
        return price.getValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass())
            return false;
        OrderItem orderItem = (OrderItem) o;
        return Objects.equals(itemId, orderItem.itemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId);
    }

}
