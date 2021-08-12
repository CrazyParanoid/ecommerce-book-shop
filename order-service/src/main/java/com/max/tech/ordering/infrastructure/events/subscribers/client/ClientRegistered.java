package com.max.tech.ordering.infrastructure.events.subscribers.client;

import com.max.tech.ordering.infrastructure.events.subscribers.Event;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClientRegistered implements Event {
    private String clientId;
    private Address address;
}
