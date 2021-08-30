package com.max.tech.ordering.domain.item;

import com.max.tech.ordering.domain.common.ValueObject;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
@Embeddable
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItemId implements ValueObject, Serializable {
    @Type(type = "org.hibernate.type.UUIDBinaryType")
    @Column(name = "item_id")
    private UUID value;

    public static OrderItemId fromValue(String value) {
        return new OrderItemId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return this.value.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass())
            return false;
        OrderItemId orderItemId = (OrderItemId) o;
        return Objects.equals(value, orderItemId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
