package com.max.tech.ordering.infrastructure.events.publisher;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.SubscribableChannel;

public interface OutputBindings {
    String OrderChannel = "orderChannel";

    @Output(OrderChannel)
    SubscribableChannel orderChannel();
}
