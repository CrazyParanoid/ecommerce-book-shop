package com.max.tech.ordering.infrastructure.events.subscribers;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface InputBindings {
    String ClientChannel = "clientChannel";
    String PaymentChannel = "paymentChannel";

    @Input(ClientChannel)
    SubscribableChannel clientChannel();

    @Input(PaymentChannel)
    SubscribableChannel paymentChannel();

}
