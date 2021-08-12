package com.max.tech.ordering.infrastructure.events.subscribers.client;

import com.max.tech.ordering.application.client.ClientService;
import com.max.tech.ordering.application.client.dto.AddressDTO;
import com.max.tech.ordering.application.client.dto.RegisterNewClientCommand;
import com.max.tech.ordering.infrastructure.events.subscribers.EventSubscriber;
import com.max.tech.ordering.infrastructure.events.subscribers.InputBindings;
import com.max.tech.ordering.web.security.User;
import com.max.tech.ordering.web.security.UserAuthentication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class ClientRegisteredEventSubscriber implements EventSubscriber<ClientRegistered> {
    private final ClientService clientService;

    public ClientRegisteredEventSubscriber(ClientService clientService) {
        this.clientService = clientService;
    }

    @Override
    @StreamListener(
            target = InputBindings.ClientChannel,
            condition = "headers['type']=='ClientRegistered'"
    )
    public void onEvent(ClientRegistered event) {
        log.info("ClientRegisteredEvent has been received");
        authorizeAsAdmin();

        var address = event.getAddress();

        clientService.registerNewClient(new RegisterNewClientCommand(
                event.getClientId(),
                new AddressDTO(
                        address.getCity(),
                        address.getStreet(),
                        address.getHouse(),
                        address.getFlat(),
                        address.getFloor(),
                        address.getEntrance()
                )
        ));
    }

    private void authorizeAsAdmin() {
        SecurityContextHolder.getContext().setAuthentication(
                new UserAuthentication(
                        new User(
                                User.SERVICE_USER_ID,
                                List.of(new User.Role(User.Role.ADMIN_ROLE))
                        )
                )
        );
    }

}
