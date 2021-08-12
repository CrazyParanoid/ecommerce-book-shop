package com.max.tech.ordering.application.client;

import com.max.tech.ordering.application.client.dto.ClientDTO;
import com.max.tech.ordering.application.client.dto.RegisterNewClientCommand;
import com.max.tech.ordering.domain.client.Client;
import com.max.tech.ordering.domain.client.ClientId;
import com.max.tech.ordering.domain.client.ClientRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@PreAuthorize("hasAuthority('ADMIN')")
public class ClientService {
    private final ClientRepository clientRepository;
    private final ClientAssembler clientAssembler;

    @Autowired
    public ClientService(ClientRepository clientRepository, ClientAssembler clientAssembler) {
        this.clientRepository = clientRepository;
        this.clientAssembler = clientAssembler;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ClientDTO registerNewClient(RegisterNewClientCommand command) {
        deduplicate(command.getClientId());

        var addressDTO = command.getAddress();
        var client = Client.newBuilder()
                .withAddress(addressDTO.getCity(),
                        addressDTO.getStreet(),
                        addressDTO.getHouse(),
                        addressDTO.getFlat(),
                        addressDTO.getFloor(),
                        addressDTO.getEntrance())
                .withId(command.getClientId())
                .build();

        clientRepository.save(client);

        log.info("Client with id {} has been created", command.getClientId());
        return clientAssembler.writeDTO(client);
    }

    private void deduplicate(String clientId) {
        var client = clientRepository.findClientById(ClientId.fromValue(clientId));
        if (client.isPresent())
            throw new IllegalStateException(String.format("Client with id %s already exists", clientId));
    }

    @Transactional(readOnly = true)
    public ClientDTO findClientById(String id) {
        return clientRepository.findClientById(ClientId.fromValue(id))
                .map(clientAssembler::writeDTO)
                .orElseThrow(() -> new ClientNotFoundException(String.format("Client with id %s is not found", id)));
    }

    @Transactional
    public void removeClient(String id) {
        var client = clientRepository.findClientById(ClientId.fromValue(id))
                .orElseThrow(() -> new ClientNotFoundException(String.format("Client with id %s is not found", id)));

        clientRepository.removeClient(client);
        log.info("Client with id {} has been removed", id);
    }
}
