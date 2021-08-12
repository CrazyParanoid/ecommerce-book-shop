package com.max.tech.ordering.infrastructure.events.subscribers.payment;

import com.max.tech.ordering.application.order.OrderService;
import com.max.tech.ordering.infrastructure.events.subscribers.EventSubscriber;
import com.max.tech.ordering.infrastructure.events.subscribers.InputBindings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentDoneEventSubscriber implements EventSubscriber<PaymentDone> {
    private final OrderService orderService;

    public PaymentDoneEventSubscriber(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    @StreamListener(
            target = InputBindings.PaymentChannel,
            condition = "headers['type']=='PaymentDone'"
    )
    public void onEvent(PaymentDone event) {
        log.info("PaymentDone has been received");

        orderService.confirmOrderPayment(event.getOrderId(), event.getPaymentId());
    }

}
