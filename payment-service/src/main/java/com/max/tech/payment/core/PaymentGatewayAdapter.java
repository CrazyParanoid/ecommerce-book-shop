package com.max.tech.payment.core;

import com.stripe.Stripe;
import com.stripe.model.Charge;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

@Slf4j
@Service
public class PaymentGatewayAdapter {
    private static final String AMOUNT = "amount";
    private static final String SOURCE = "source";
    private static final String CURRENCY = "currency";
    private static final String USD = "usd";

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @PostConstruct
    private void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    @SneakyThrows
    public String makePayment(PaymentRequest request) {
        var charge = Charge.create(
                Map.of(
                        AMOUNT, request.getAmount(),
                        CURRENCY, USD,
                        SOURCE, request.getToken()
                )
        );
        log.info("Payment with id {} has been created", charge.getId());
        return charge.getId();
    }

}
