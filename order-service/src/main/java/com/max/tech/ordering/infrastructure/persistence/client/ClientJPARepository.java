package com.max.tech.ordering.infrastructure.persistence.client;

import com.max.tech.ordering.domain.client.Client;
import com.max.tech.ordering.domain.client.ClientId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientJPARepository extends JpaRepository<Client, Long> {

    Optional<Client> findByClientId(ClientId clientId);

}
