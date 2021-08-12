package com.max.tech.payment.integration;

import com.max.tech.payment.Application;
import com.max.tech.payment.TestValues;
import com.max.tech.payment.core.Payment;
import com.max.tech.payment.core.PaymentRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ActiveProfiles("it")
@SpringBootTest(classes = Application.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PaymentRepositoryIT {
    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    public void test_save_payment(){
        var payment = Payment.newPayment(
                TestValues.PAYMENT_ID,
                TestValues.ORDER_ID,
                TestValues.AMOUNT,
                TestValues.CLIENT_ID
        );

        paymentRepository.save(payment);

        var savedPayment = paymentRepository.findById(TestValues.PAYMENT_ID);
        Assertions.assertTrue(savedPayment.isPresent());
    }

}
