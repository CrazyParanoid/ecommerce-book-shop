package com.max.tech.ordering.application;

import com.max.tech.ordering.application.client.ClientAssembler;
import com.max.tech.ordering.application.client.ClientService;
import com.max.tech.ordering.application.client.dto.ClientDTO;
import com.max.tech.ordering.domain.TestDomainObjectsFactory;
import com.max.tech.ordering.domain.client.Client;
import com.max.tech.ordering.domain.client.ClientId;
import com.max.tech.ordering.domain.client.ClientRepository;
import com.max.tech.ordering.util.TestValues;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

public class ClientServiceTest {
    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientService clientService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        clientService = new ClientService(clientRepository, new ClientAssembler());
    }

    @Test
    public void test_register_new_client() {
        Mockito.doNothing().when(clientRepository).save(ArgumentMatchers.any(Client.class));

        var clientDTO = clientService.registerNewClient(TestApplicationObjectsFactory.newRegisterNewClientCommand());

        assertClientDTO(clientDTO);
    }

    @Test
    public void test_find_client() {
        Mockito.when(clientRepository.findClientById(ArgumentMatchers.any(ClientId.class)))
                .thenReturn(Optional.of(TestDomainObjectsFactory.newClient()));

        var clientDTO = clientService.findClientById(TestValues.CLIENT_ID);

        assertClientDTO(clientDTO);
    }

    @Test
    public void test_delete_client(){
        Mockito.when(clientRepository.findClientById(ArgumentMatchers.any(ClientId.class)))
                .thenReturn(Optional.of(TestDomainObjectsFactory.newClient()));
        Mockito.doNothing().when(clientRepository).removeClient(ArgumentMatchers.any(Client.class));

        clientService.removeClient(TestValues.CLIENT_ID);

        Mockito.verify(clientRepository, Mockito.times(1)).removeClient(ArgumentMatchers.any(Client.class));
    }

    private void assertClientDTO(ClientDTO clientDTO) {
        var address = clientDTO.getAddress();
        Assertions.assertNotNull(clientDTO);
        Assertions.assertEquals(clientDTO.getClientId(), TestValues.CLIENT_ID);
        Assertions.assertNotNull(address);
        Assertions.assertEquals(address.getCity(), TestValues.CITY);
        Assertions.assertEquals(address.getStreet(), TestValues.STREET);
        Assertions.assertEquals(address.getFlat(), TestValues.FLAT);
        Assertions.assertEquals(address.getFloor(), TestValues.FLOOR);
        Assertions.assertEquals(address.getEntrance(), TestValues.ENTRANCE);
        Assertions.assertEquals(address.getHouse(), TestValues.HOUSE);
    }

}
