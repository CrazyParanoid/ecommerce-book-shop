package com.max.tech.ordering.application.order.dto;

import com.max.tech.ordering.application.Json;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Selected product")
public class ProductDTO implements Json {
    @ApiModelProperty(value = "Product id", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private String productId;
    @ApiModelProperty(value = "Product price", accessMode = ApiModelProperty.AccessMode.READ_ONLY, position = 1)
    private BigDecimal price;
    @ApiModelProperty(value = "Product quantity", accessMode = ApiModelProperty.AccessMode.READ_ONLY, position = 2)
    private Integer quantity;
}
