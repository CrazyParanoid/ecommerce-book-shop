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
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Amount implements ValueObject, Comparable<Amount> {
    private BigDecimal value;

    @Transient
    public static Amount ZERO_AMOUNT = Amount.fromValue(BigDecimal.ZERO);

    public Amount multiply(Double value) {
        if (value < 0)
            throw new IllegalArgumentException(String.format("Can't perform multiplication, " +
                    "value %f must be positive", value));
        return Amount.fromValue(this.value.multiply(BigDecimal.valueOf(value)));
    }

    public Amount subtract(Amount amount) {
        if (amount.value.signum() == -1)
            throw new IllegalArgumentException(String.format("Can't perform subtraction operation," +
                    " value %s must be positive", amount));
        if (this.compareTo(amount) < 0)
            throw new IllegalArgumentException(String.format("Can't perform subtraction operation," +
                    " value %s must be less than the decremented value", amount));

        return Amount.fromValue(this.value.subtract(amount.value));
    }

    public Amount add(Amount amount) {
        if (amount.value.signum() == -1)
            throw new IllegalArgumentException(String.format("Can't perform addition operation," +
                    " value %s must be positive", amount));
        return Amount.fromValue(this.value.add(amount.value));
    }

    public Boolean greaterOrEquals(Amount amount) {
        return this.compareTo(amount) > 0;
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

    @Override
    public String toString() {
        return this.value.toString();
    }

    @Override
    public int compareTo(Amount o) {
        return this.value.compareTo(o.value);
    }

}
