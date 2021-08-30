package com.max.tech.ordering.application.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Selected item")
public class OrderItemDTO implements Json {
    @ApiModelProperty(value = "Item id", required = true)
    @NotBlank(message = "ItemId can't be null or empty")
    private String itemId;
    @ApiModelProperty(value = "Item price", required = true, position = 1)
    @NotNull(message = "price can't be null")
    private BigDecimal price;
    @ApiModelProperty(value = "Item quantity", required = true, position = 2)
    @NotNull(message = "quantity can't be null")
    private Integer quantity;
}
