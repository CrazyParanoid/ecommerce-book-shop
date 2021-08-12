package com.max.tech.ordering.domain.client;

import java.util.Optional;

public interface ClientRepository {

    void save(Client client);

    Optional<Client> findClientById(ClientId clientId);

    void removeClient(Client client);

}
