package com.max.tech.payment.core.events;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.SubscribableChannel;

public interface OutputBindings {
    String PaymentChannel = "paymentChannel";

    @Output(PaymentChannel)
    SubscribableChannel paymentChannel();
}
