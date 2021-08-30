package com.max.tech.ordering.application.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "Order with items")
public class OrderDTO extends RepresentationModel<OrderDTO> implements Json {
    @ApiModelProperty(value = "Order id", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private String orderId;
    @ApiModelProperty(value = "Order status", accessMode = ApiModelProperty.AccessMode.READ_ONLY, position = 1)
    private String status;
    @ApiModelProperty(value = "Client id", accessMode = ApiModelProperty.AccessMode.READ_ONLY, position = 2)
    private String clientId;
    @ApiModelProperty(value = "Courier id", accessMode = ApiModelProperty.AccessMode.READ_ONLY, position = 3)
    private String courierId;
    @ApiModelProperty(value = "Delivery date", accessMode = ApiModelProperty.AccessMode.READ_ONLY, position = 4)
    private LocalDateTime deliveredAt;
    @ApiModelProperty(value = "Total order price", accessMode = ApiModelProperty.AccessMode.READ_ONLY, position = 5)
    private BigDecimal totalPrice;
    @ApiModelProperty(value = "Payment id", accessMode = ApiModelProperty.AccessMode.READ_ONLY, position = 6)
    private String paymentId;
    @ApiModelProperty(value = "Delivery address id", accessMode = ApiModelProperty.AccessMode.READ_ONLY, position = 7)
    private String deliveryAddressId;
    @ApiModelProperty(value = "Item list", accessMode = ApiModelProperty.AccessMode.READ_ONLY, position = 8)
    private Set<OrderItemDTO> items;

    public OrderDTO(String orderId, String status, String clientId, String deliveryAddressId,
                    LocalDateTime deliveredAt, BigDecimal totalPrice) {
        this.orderId = orderId;
        this.deliveryAddressId = deliveryAddressId;
        this.status = status;
        this.clientId = clientId;
        this.deliveredAt = deliveredAt;
        this.totalPrice = totalPrice;
    }

}
