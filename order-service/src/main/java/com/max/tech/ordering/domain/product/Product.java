package com.max.tech.ordering.domain.product;

import com.max.tech.ordering.domain.Amount;
import com.max.tech.ordering.domain.Order;
import com.max.tech.ordering.domain.common.Entity;
import com.max.tech.ordering.domain.common.IdentifiedDomainObject;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Getter
@AllArgsConstructor
@Table(name = "products")
@javax.persistence.Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends IdentifiedDomainObject implements Entity {
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "product_id"))
    private ProductId productId;
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "price"))
    private Amount price;
    private Integer quantity;
    @Setter
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order = null;

    public void update(Amount price, Integer quantity) {
        this.quantity = quantity;
        this.price = price;
        validate();
    }

    public Amount totalPrice() {
        return this.price.multiply(
                Double.valueOf(this.quantity)
        );
    }

    public void validate() {
        if (this.quantity <= 0)
            throw new IllegalArgumentException("Quantity must be greater than 0");
        if (this.price.isNegativeOrZero())
            throw new IllegalArgumentException("Price must be greater than 0");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Product product = (Product) o;
        return Objects.equals(productId, product.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }

}
