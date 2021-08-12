package com.max.tech.ordering.domain;

import com.max.tech.ordering.domain.common.ValueObject;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@Embeddable
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Amount implements ValueObject {
    private BigDecimal value;

    @Transient
    public static Amount ZERO_AMOUNT = Amount.fromValue(BigDecimal.ZERO);

    public Amount multiply(Double value) {
        return Amount.fromValue(this.value.multiply(BigDecimal.valueOf(value)));
    }

    public Amount subtract(Amount amount) {
        return Amount.fromValue(this.value.subtract(amount.value));
    }

    public Amount add(Amount amount) {
        return Amount.fromValue(this.value.add(amount.value));
    }

    public Boolean greaterOrEquals(Amount amount) {
        return this.value.compareTo(amount.value) > 0;
    }

    public boolean isNegativeOrZero() {
        return this.value.signum() != 1;
    }

    public static Amount fromValue(BigDecimal value) {
        return new Amount(value.setScale(6, RoundingMode.UP));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Amount amount = (Amount) o;
        return this.value.equals(amount.value);
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }
}
