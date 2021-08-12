package com.max.tech.ordering.application.client.dto;

import com.max.tech.ordering.application.Json;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Request for registration of new client")
public class RegisterNewClientCommand implements Json {
    @NotEmpty(message = "client_id can't be null or empty")
    @ApiModelProperty(value = "Client id", required = true)
    private String clientId;
    @Valid
    @NotNull(message = "address can't be null")
    @ApiModelProperty(value = "Address", required = true, position = 1)
    private AddressDTO address;
}
