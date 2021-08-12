package com.max.tech.payment.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PaymentRequest {
    @NotEmpty(message = "order_id can't be null or empty")
    private String orderId;
    @NotEmpty(message = "token can't be null or empty")
    private String token;
    @NotNull(message = "amount can't be null")
    private Long amount;
    @JsonIgnore
    private String clientId;
}
