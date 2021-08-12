package com.max.tech.ordering.infrastructure.persistence.client;

import com.max.tech.ordering.domain.client.Client;
import com.max.tech.ordering.domain.client.ClientId;
import com.max.tech.ordering.domain.client.ClientRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ClientRepositoryImpl implements ClientRepository {
    private final ClientJPARepository clientJPARepository;

    public ClientRepositoryImpl(ClientJPARepository clientJPARepository) {
        this.clientJPARepository = clientJPARepository;
    }

    @Override
    public void save(Client client) {
        clientJPARepository.save(client);
    }

    @Override
    public Optional<Client> findClientById(ClientId clientId) {
        return clientJPARepository.findByClientId(clientId);
    }

    @Override
    public void removeClient(Client client) {
        clientJPARepository.delete(client);
    }

}
