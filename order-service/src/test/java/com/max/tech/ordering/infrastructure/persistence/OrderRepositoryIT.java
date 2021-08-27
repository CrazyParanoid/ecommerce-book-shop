package com.max.tech.ordering.infrastructure.persistence;

import com.max.tech.ordering.Application;
import com.max.tech.ordering.domain.OrderRepository;
import com.max.tech.ordering.domain.TestDomainObjectsFactory;
import com.max.tech.ordering.domain.person.PersonId;
import com.max.tech.ordering.util.TestValues;
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
public class OrderRepositoryIT {
    @Autowired
    private OrderRepository orderRepository;

    @Test
    public void test_save_new_order() {
        var order = TestDomainObjectsFactory.newOrderWithOneProduct();

        orderRepository.save(order);

        var persistedOrder = orderRepository.findOrderById(order.getOrderId());
        Assertions.assertTrue(persistedOrder.isPresent());
    }

    @Test
    public void test_find_pending_payment_orders() {
        var order = TestDomainObjectsFactory.newOrderWithOneProduct();
        orderRepository.save(order);

        var orders = orderRepository.findPendingPaymentOrdersForClient(PersonId.fromValue(TestValues.CLIENT_ID));

        Assertions.assertFalse(orders.isEmpty());
        Assertions.assertEquals(orders.size(), 1);
    }

}
