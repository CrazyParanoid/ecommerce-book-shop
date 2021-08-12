package com.max.tech.payment.web;

import com.max.tech.payment.core.events.EventPublisher;
import com.max.tech.payment.core.events.PaymentDone;
import com.max.tech.payment.core.Payment;
import com.max.tech.payment.core.PaymentRepository;
import com.max.tech.payment.core.PaymentGatewayAdapter;
import com.max.tech.payment.core.PaymentRequest;
import com.max.tech.payment.web.security.User;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/v1/payments")
@Tag(name = "payment", description = "The payment REST API")
public class PaymentController {
    private final PaymentRepository paymentRepository;
    private final EventPublisher eventPublisher;
    private final PaymentGatewayAdapter paymentGatewayAdapter;

    @Autowired
    public PaymentController(PaymentRepository paymentRepository, EventPublisher eventPublisher,
                             PaymentGatewayAdapter paymentGatewayAdapter) {
        this.paymentRepository = paymentRepository;
        this.eventPublisher = eventPublisher;
        this.paymentGatewayAdapter = paymentGatewayAdapter;
    }

    @PostMapping
    @SneakyThrows
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Make new payment")
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('BUYER')")
    public Payment postPayment(@RequestBody @Valid PaymentRequest request) {
        var paymentId = paymentGatewayAdapter.makePayment(request);
        var payment = Payment.newPayment(
                paymentId,
                request.getOrderId(),
                request.getAmount(),
                extractClientId()
        );

        paymentRepository.save(payment);
        eventPublisher.publish(new PaymentDone(payment.getOrderId().toString(), payment.getId()));

        return payment;
    }

    private String extractClientId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var user = (User) authentication.getPrincipal();
        return user.getId();
    }

}
