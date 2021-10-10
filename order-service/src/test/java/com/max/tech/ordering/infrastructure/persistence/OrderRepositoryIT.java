package com.max.tech.ordering.infrastructure.persistence;

import com.max.tech.ordering.domain.OrderRepository;
import com.max.tech.ordering.domain.TestDomainObjectsFactory;
import com.max.tech.ordering.domain.person.PersonId;
import com.max.tech.ordering.helper.TestValues;
import com.max.tech.ordering.infrastructure.config.ApplicationConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;


@Transactional
@ActiveProfiles("it")
@DataJpaTest(
        includeFilters = @ComponentScan.Filter(
                type = ASSIGNABLE_TYPE,
                classes = ApplicationConfig.class)
)
public class OrderRepositoryIT {
    @Autowired
    private OrderRepository orderRepository;

    @Test
    public void shouldSaveNewOrder() {
        var order = TestDomainObjectsFactory.newOrderWithOneItem();

        orderRepository.save(order);

        var persistedOrder = orderRepository.findOrderById(order.getOrderId());
        Assertions.assertTrue(persistedOrder.isPresent());
    }

    @Test
    public void shouldReturnPendingPaymentsOrders() {
        var order = TestDomainObjectsFactory.newOrderWithOneItem();
        orderRepository.save(order);

        var orders = orderRepository.findPendingPaymentOrdersForClient(PersonId.fromValue(TestValues.CLIENT_ID));

        Assertions.assertFalse(orders.isEmpty());
        Assertions.assertEquals(orders.size(), 1);
    }

}
