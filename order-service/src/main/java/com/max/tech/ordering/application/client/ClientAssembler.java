package com.max.tech.ordering.application.client;

import com.max.tech.ordering.application.client.dto.AddressDTO;
import com.max.tech.ordering.application.client.dto.ClientDTO;
import com.max.tech.ordering.domain.client.Client;
import org.springframework.stereotype.Service;

@Service
public class ClientAssembler {

    public ClientDTO writeDTO(Client client) {
        var address = client.getAddress();
        return new ClientDTO(
                client.getClientId().toString(),
                new AddressDTO(
                        address.getCity(),
                        address.getStreet(),
                        address.getHouse(),
                        address.getFlat(),
                        address.getFloor(),
                        address.getEntrance()
                )
        );
    }

}
