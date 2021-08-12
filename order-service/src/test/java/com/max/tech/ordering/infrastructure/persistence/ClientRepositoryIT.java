package com.max.tech.ordering.infrastructure.persistence;

import com.max.tech.ordering.Application;
import com.max.tech.ordering.util.TestValues;
import com.max.tech.ordering.domain.TestDomainObjectsFactory;
import com.max.tech.ordering.domain.client.ClientId;
import com.max.tech.ordering.domain.client.ClientRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ActiveProfiles("it")
@SpringBootTest(classes = Application.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ClientRepositoryIT {
    @Autowired
    private ClientRepository clientRepository;

    @Test
    public void test_save_client() {
        var client = TestDomainObjectsFactory.newClient();

        clientRepository.save(client);

        var savedClient = clientRepository.findClientById(ClientId.fromValue(TestValues.CLIENT_ID));
        Assertions.assertTrue(savedClient.isPresent());
    }

    @Test
    public void test_delete_client() {
        var client = TestDomainObjectsFactory.newClient();
        clientRepository.save(client);

        clientRepository.removeClient(client);

        var savedClient = clientRepository.findClientById(ClientId.fromValue(TestValues.CLIENT_ID));
        Assertions.assertFalse(savedClient.isPresent());
    }

}
