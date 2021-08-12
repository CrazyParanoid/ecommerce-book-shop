package com.max.tech.ordering.application.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.max.tech.ordering.application.Json;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Request to add an product to an order")
public class AddProductToOrderCommand implements Json {
    @JsonIgnore
    private String orderId;
    @ApiModelProperty(value = "Product id", required = true)
    @NotBlank(message = "product_id can't be null or empty")
    private String productId;
    @ApiModelProperty(value = "Product price", required = true, position = 1)
    @NotNull(message = "price can't be null")
    private BigDecimal price;
    @ApiModelProperty(value = "Product quantity", required = true, position = 2)
    @NotNull(message = "quantity can't be null")
    private Integer quantity;
}
