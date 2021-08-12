package com.max.tech.ordering.application.client.dto;

import com.max.tech.ordering.application.Json;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Client")
public class AddressDTO implements Json {
    @NotEmpty(message = "city can't be null or empty")
    @ApiModelProperty(value = "city", required = true, position = 1)
    private String city;
    @NotEmpty(message = "street can't be null or empty")
    @ApiModelProperty(value = "street", required = true, position = 2)
    private String street;
    @NotEmpty(message = "house can't be null or empty")
    @ApiModelProperty(value = "house", required = true, position = 3)
    private String house;
    @ApiModelProperty(value = "flat", position = 4)
    private Integer flat;
    @ApiModelProperty(value = "floor", position = 5)
    private Integer floor;
    @ApiModelProperty(value = "entrance", position = 6)
    private Integer entrance;
}
