package com.max.tech.catalog.catalog.order;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface InputBindings {
    String OrderChannel = "orderChannel";

    @Input(OrderChannel)
    SubscribableChannel orderChannel();

}
