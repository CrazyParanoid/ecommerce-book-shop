package com.max.tech.ordering.domain.client;

import com.max.tech.ordering.domain.common.ValueObject;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClientId implements ValueObject, Serializable {
    @Type(type = "org.hibernate.type.UUIDBinaryType")
    @Column(name = "client_id")
    private UUID value;

    public static ClientId fromValue(String value) {
        return new ClientId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return this.value.toString();
    }

}
