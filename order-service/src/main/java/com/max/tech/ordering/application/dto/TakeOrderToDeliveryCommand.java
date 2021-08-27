package com.max.tech.ordering.application.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.max.tech.ordering.application.Json;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TakeOrderToDeliveryCommand implements Json {
    @JsonIgnore
    private String orderId;
    @JsonIgnore
    private String courierId;
}
