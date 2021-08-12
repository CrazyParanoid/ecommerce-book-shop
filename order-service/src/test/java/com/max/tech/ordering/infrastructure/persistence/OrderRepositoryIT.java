package com.max.tech.ordering.infrastructure.persistence;

import com.max.tech.ordering.Application;
import com.max.tech.ordering.util.TestValues;
import com.max.tech.ordering.domain.Address;
import com.max.tech.ordering.domain.OrderRepository;
import com.max.tech.ordering.domain.TestDomainObjectsFactory;
import com.max.tech.ordering.domain.client.ClientId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Transactional
@ActiveProfiles("it")
@SpringBootTest(classes = Application.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class OrderRepositoryIT {
    @Autowired
    private OrderRepository orderRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void test_save_new_order() {
        var order = TestDomainObjectsFactory.newOrderWithOneProduct();
        saveAddress(order.getDeliveryAddress());

        orderRepository.save(order);

        var persistedOrder = orderRepository.findOrderById(order.getOrderId());
        Assertions.assertTrue(persistedOrder.isPresent());
    }

    @Test
    public void test_find_pending_products_orders() {
        var order = TestDomainObjectsFactory.newOrderWithOneProduct();
        saveAddress(order.getDeliveryAddress());
        orderRepository.save(order);

        var orders = orderRepository.findPendingProductsOrdersForClient(ClientId.fromValue(TestValues.CLIENT_ID));

        Assertions.assertFalse(orders.isEmpty());
        Assertions.assertEquals(orders.size(), 1);
    }

    private void saveAddress(Address address) {
        entityManager.merge(address);
        entityManager.flush();
    }
}
