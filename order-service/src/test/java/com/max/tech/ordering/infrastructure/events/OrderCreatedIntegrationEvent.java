package com.max.tech.ordering.infrastructure.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OrderCreatedIntegrationEvent {
    private String orderId;
    private String clientId;
    private Address deliveryAddress;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Address{
        private String city;
        private String street;
        private String house;
        private Integer flat;
        private Integer floor;
        private Integer entrance;
    }
}
