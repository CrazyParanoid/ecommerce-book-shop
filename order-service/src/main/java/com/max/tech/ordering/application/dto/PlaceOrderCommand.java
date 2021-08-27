package com.max.tech.ordering.application.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.max.tech.ordering.application.Json;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Request to create order")
public class PlaceOrderCommand implements Json {
    @JsonIgnore
    private String clientId;
    @NotEmpty(message = "delivery_address_id can't be null or empty")
    @ApiModelProperty(value = "delivery address id", required = true)
    private String deliveryAddressId;
    @NotEmpty(message = "products can't be null or empty")
    @ApiModelProperty(value = "Selected products", required = true, position = 1)
    private List<@Valid ProductDTO> products;
}
