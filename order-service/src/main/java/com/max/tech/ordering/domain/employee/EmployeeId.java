package com.max.tech.ordering.domain.employee;

import com.max.tech.ordering.domain.common.ValueObject;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.util.UUID;

@Embeddable
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmployeeId implements ValueObject {
    private UUID value;

    public static EmployeeId fromValue(String value) {
        return new EmployeeId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return this.value.toString();
    }

}
