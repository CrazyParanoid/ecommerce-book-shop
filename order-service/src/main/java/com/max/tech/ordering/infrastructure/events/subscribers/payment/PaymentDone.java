package com.max.tech.ordering.infrastructure.events.subscribers.payment;

import com.max.tech.ordering.infrastructure.events.subscribers.Event;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentDone implements Event {
    private String orderId;
    private String paymentId;
}
