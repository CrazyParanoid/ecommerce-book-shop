package com.max.tech.ordering.application.client.dto;

import com.max.tech.ordering.application.Json;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Client")
@EqualsAndHashCode(callSuper = true)
public class ClientDTO extends RepresentationModel<ClientDTO> implements Json {
    @ApiModelProperty(value = "Client id", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private String clientId;
    @ApiModelProperty(value = "Address", accessMode = ApiModelProperty.AccessMode.READ_ONLY, position = 1)
    private AddressDTO address;
}
