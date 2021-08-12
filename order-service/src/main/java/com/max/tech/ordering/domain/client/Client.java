package com.max.tech.ordering.domain.client;

import com.max.tech.ordering.domain.Address;
import com.max.tech.ordering.domain.common.Entity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Table(name = "clients")
@javax.persistence.Entity
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Client implements Entity {
    @EmbeddedId
    private ClientId clientId;
    @JoinColumn(name = "address_id")
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Address address;

    public static Builder newBuilder() {
        return new Client().new Builder();
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public class Builder {

        public Builder withAddress(String city,
                                   String street,
                                   String house,
                                   Integer flat,
                                   Integer floor,
                                   Integer entrance) {
            Client.this.address = new Address(city, street, house, flat, floor, entrance);
            return this;
        }

        public Builder withId(String clientId) {
            Client.this.clientId = ClientId.fromValue(clientId);
            return this;
        }

        public Client build() {
            return Client.this;
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return Objects.equals(clientId, client.clientId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId);
    }
}
