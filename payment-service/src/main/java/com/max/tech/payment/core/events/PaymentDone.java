package com.max.tech.payment.core.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDone implements Event {
    private String orderId;
    private String paymentId;
}
