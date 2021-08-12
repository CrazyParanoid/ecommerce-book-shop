package com.max.tech.ordering.domain;

import com.max.tech.ordering.domain.common.DomainEvent;
import com.max.tech.ordering.domain.employee.EmployeeId;
import com.max.tech.ordering.domain.payment.PaymentId;
import com.max.tech.ordering.domain.product.Product;
import com.max.tech.ordering.domain.product.ProductId;
import com.max.tech.ordering.util.AssertionUtil;
import com.max.tech.ordering.util.TestValues;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class OrderTest {

    @Test
    public void test_create_new_order() {
        var order = TestDomainObjectsFactory.newOrder();
        var orderCreatedDomainEvent = getDomainEventByType(order, OrderCreated.class);

        assertNewOrder(order);
        assertOrderCreatedDomainEvent(orderCreatedDomainEvent);
    }

    private void assertNewOrder(Order order) {
        var products = order.getProducts();
        Assertions.assertNotNull(products);
        Assertions.assertNull(order.getPaymentId());
        Assertions.assertNull(order.getCourierId());
        Assertions.assertNull(order.getDeliveredAt());
        Assertions.assertTrue(products.isEmpty());
        Assertions.assertEquals(order.getStatus(), Order.Status.PENDING_FOR_PRODUCTS);
        Assertions.assertNotNull(order.getOrderId());
        Assertions.assertEquals(order.getClientId().toString(), TestValues.CLIENT_ID);
        Assertions.assertEquals(order.getTotalPrice(), Amount.ZERO_AMOUNT);
        AssertionUtil.assertAddress(order.getDeliveryAddress());
    }

    private void assertOrderCreatedDomainEvent(OrderCreated domainEvent) {
        Assertions.assertNotNull(domainEvent.getOrderId());
        Assertions.assertEquals(domainEvent.getClientId(), TestValues.CLIENT_ID);
        AssertionUtil.assertAddress(domainEvent.getDeliveryAddress());
    }

    @Test
    public void test_add_one_product_to_order() {
        var order = TestDomainObjectsFactory.newOrder();

        order.addProduct(
                ProductId.fromValue(TestValues.FIRST_PRODUCT_ID),
                Amount.fromValue(TestValues.FIRST_PRODUCT_PRICE),
                TestValues.FIRST_PRODUCT_QUANTITY
        );

        assertOrderWithOneProduct(order);
        Assertions.assertEquals(order.getTotalPrice().getValue(), TestValues.TOTAL_ORDER_PRICE_WITH_ONE_PRODUCT);
    }

    @Test
    public void test_add_product_with_zero_quantity() {
        var order = TestDomainObjectsFactory.newOrder();

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> order.addProduct(
                        ProductId.fromValue(TestValues.FIRST_PRODUCT_ID),
                        Amount.fromValue(TestValues.FIRST_PRODUCT_PRICE),
                        0
                ),
                "Quantity must be greater than 0");
    }

    @Test
    public void test_add_product_with_zero_price() {
        var order = TestDomainObjectsFactory.newOrder();

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> order.addProduct(
                        ProductId.fromValue(TestValues.FIRST_PRODUCT_ID),
                        Amount.ZERO_AMOUNT,
                        TestValues.FIRST_PRODUCT_QUANTITY
                ),
                "Price must be greater than 0");
    }

    private void assertOrderWithOneProduct(Order order) {
        var domainEvent = getDomainEventByType(order, ProductAddedToOrder.class);
        var product = order.findProductById(ProductId.fromValue(TestValues.FIRST_PRODUCT_ID));

        assertProduct(product, TestValues.FIRST_PRODUCT_ID,
                TestValues.FIRST_PRODUCT_PRICE,
                TestValues.FIRST_PRODUCT_QUANTITY);
        assertProductAddedToOrderDomainEvent(domainEvent, TestValues.FIRST_PRODUCT_ID,
                TestValues.TOTAL_ORDER_PRICE_WITH_ONE_PRODUCT,
                TestValues.FIRST_PRODUCT_QUANTITY);
    }

    @Test
    public void test_add_two_products_to_order() {
        var order = TestDomainObjectsFactory.newOrder();

        order.addProduct(
                ProductId.fromValue(TestValues.FIRST_PRODUCT_ID),
                Amount.fromValue(TestValues.FIRST_PRODUCT_PRICE),
                TestValues.FIRST_PRODUCT_QUANTITY
        );
        order.addProduct(
                ProductId.fromValue(TestValues.SECOND_PRODUCT_ID),
                Amount.fromValue(TestValues.SECOND_PRODUCT_PRICE),
                TestValues.SECOND_PRODUCT_QUANTITY
        );

        assertOrderWithTwoProducts(order);
        Assertions.assertEquals(order.getTotalPrice().getValue(), TestValues.TOTAL_ORDER_PRICE_WITH_TWO_PRODUCTS);
    }

    @Test
    public void test_update_existed_product() {
        var order = TestDomainObjectsFactory.newOrderWithOneProduct();

        order.addProduct(
                ProductId.fromValue(TestValues.FIRST_PRODUCT_ID),
                Amount.fromValue(TestValues.SECOND_PRODUCT_PRICE),
                TestValues.SECOND_PRODUCT_QUANTITY
        );
        var product = order.findProductById(ProductId.fromValue(TestValues.FIRST_PRODUCT_ID));

        Assertions.assertEquals(order.getTotalPrice().getValue(), TestValues.TOTAL_ORDER_PRICE_WITH_UPDATED_ONE_PRODUCT);
        assertProduct(product, TestValues.FIRST_PRODUCT_ID,
                TestValues.SECOND_PRODUCT_PRICE,
                TestValues.SECOND_PRODUCT_QUANTITY);
    }

    private void assertOrderWithTwoProducts(Order order) {
        var domainEvents = getDomainEventsByType(order, ProductAddedToOrder.class);
        var firstProduct = order.findProductById(ProductId.fromValue(TestValues.FIRST_PRODUCT_ID));
        var secondProduct = order.findProductById(ProductId.fromValue(TestValues.SECOND_PRODUCT_ID));

        assertProduct(firstProduct, TestValues.FIRST_PRODUCT_ID,
                TestValues.FIRST_PRODUCT_PRICE,
                TestValues.FIRST_PRODUCT_QUANTITY);
        assertProduct(secondProduct, TestValues.SECOND_PRODUCT_ID,
                TestValues.SECOND_PRODUCT_PRICE,
                TestValues.SECOND_PRODUCT_QUANTITY);

        assertProductAddedToOrderDomainEvent(domainEvents.get(0), TestValues.FIRST_PRODUCT_ID,
                TestValues.TOTAL_ORDER_PRICE_WITH_ONE_PRODUCT,
                TestValues.FIRST_PRODUCT_QUANTITY);
        assertProductAddedToOrderDomainEvent(domainEvents.get(1), TestValues.SECOND_PRODUCT_ID,
                TestValues.TOTAL_ORDER_PRICE_WITH_TWO_PRODUCTS,
                TestValues.SECOND_PRODUCT_QUANTITY);
    }

    private void assertProduct(Product product, String productId, BigDecimal amount, Integer quantity) {
        Assertions.assertNotNull(product);
        Assertions.assertEquals(product.getProductId().toString(), productId);
        Assertions.assertEquals(product.getPrice().getValue(), amount);
        Assertions.assertEquals(product.getQuantity(), quantity);
    }

    private void assertProductAddedToOrderDomainEvent(ProductAddedToOrder domainEvent, String productId,
                                                      BigDecimal amount, Integer quantity) {
        Assertions.assertEquals(domainEvent.getProductId(), productId);
        Assertions.assertEquals(domainEvent.getTotalPrice(), amount);
        Assertions.assertEquals(domainEvent.getQuantity(), quantity);
        Assertions.assertNotNull(domainEvent.getOrderId());
    }

    @Test
    public void test_add_product_to_delivered_order() {
        var order = TestDomainObjectsFactory.newDeliveredOrder();

        Assertions.assertThrows(IllegalStateException.class,
                () -> order.addProduct(
                        ProductId.fromValue(TestValues.FIRST_PRODUCT_ID),
                        Amount.fromValue(TestValues.FIRST_PRODUCT_PRICE),
                        TestValues.FIRST_PRODUCT_QUANTITY
                ),
                "Wrong invocation for current state");
    }

    @Test
    public void test_remove_product_from_order() {
        var order = TestDomainObjectsFactory.newOrderWithOneProduct();

        order.removeProduct(ProductId.fromValue(TestValues.FIRST_PRODUCT_ID));
        var domainEvent = getDomainEventByType(order, ProductRemovedFromOrder.class);

        assertProductRemovedFromOrderDomainEvent(domainEvent);
        Assertions.assertTrue(order.getProducts().isEmpty());
        Assertions.assertEquals(order.getTotalPrice(), Amount.ZERO_AMOUNT);
    }

    @Test
    public void test_remove_product_from_order_with_discount() {
        var order = TestDomainObjectsFactory.newOrderWithDiscount();

        order.removeProduct(ProductId.fromValue(TestValues.FIRST_PRODUCT_ID));
        var domainEvent = getDomainEventByType(order, ProductRemovedFromOrder.class);

        assertProductRemovedFromOrderDomainEvent(domainEvent);
        Assertions.assertTrue(order.getProducts().isEmpty());
        Assertions.assertEquals(order.getTotalPrice(), Amount.ZERO_AMOUNT);
    }

    private void assertProductRemovedFromOrderDomainEvent(ProductRemovedFromOrder domainEvent) {
        Assertions.assertNotNull(domainEvent.getOrderId());
        Assertions.assertEquals(domainEvent.getProductId(), TestValues.FIRST_PRODUCT_ID);
        Assertions.assertEquals(domainEvent.getTotalPrice(), Amount.ZERO_AMOUNT.getValue());
    }

    @Test
    public void test_remove_product_from_empty_order() {
        var order = TestDomainObjectsFactory.newOrder();

        Assertions.assertThrows(IllegalStateException.class,
                () -> order.removeProduct(ProductId.fromValue(TestValues.FIRST_PRODUCT_ID)),
                "Order products can't be empty");
    }

    @Test
    public void test_clear_order() {
        var order = TestDomainObjectsFactory.newOrderWithOneProduct();

        order.clearProducts();
        var domainEvent = getDomainEventByType(order, OrderCleaned.class);

        Assertions.assertNotNull(domainEvent);
        Assertions.assertNotNull(domainEvent.getOrderId());
        Assertions.assertTrue(order.getProducts().isEmpty());
    }

    @Test
    public void test_confirm_order_payment() {
        var order = TestDomainObjectsFactory.newOrderWithOneProduct();

        order.confirmPayment(new PaymentId(TestValues.PAYMENT_ID));
        var domainEvent = getDomainEventByType(order, OrderPaid.class);

        Assertions.assertEquals(order.getStatus(), Order.Status.PENDING_DELIVERY_SERVICE);
        Assertions.assertNotNull(domainEvent);
        Assertions.assertNotNull(domainEvent.getOrderId());
        Assertions.assertEquals(domainEvent.getPaymentId().toString(), TestValues.PAYMENT_ID);
    }

    @Test
    public void test_send_empty_order_to_delivery_service() {
        var order = TestDomainObjectsFactory.newOrder();

        Assertions.assertThrows(IllegalStateException.class,
                () -> order.confirmPayment(new PaymentId(TestValues.PAYMENT_ID)),
                "Order products can't be empty");
    }

    @Test
    public void test_take_order_in_delivery() {
        var order = TestDomainObjectsFactory.newPendingDeliveryServiceOrder();

        order.takeInDelivery(EmployeeId.fromValue(TestValues.EMPLOYEE_ID));
        var domainEvent = getDomainEventByType(order, OrderTookInDelivery.class);

        Assertions.assertEquals(order.getStatus(), Order.Status.PENDING_FOR_DELIVERING);
        Assertions.assertEquals(order.getCourierId().toString(), TestValues.EMPLOYEE_ID);
        assertOrderTookInDeliveryDomainEvent(domainEvent);
    }

    private void assertOrderTookInDeliveryDomainEvent(OrderTookInDelivery domainEvent) {
        Assertions.assertNotNull(domainEvent);
        Assertions.assertNotNull(domainEvent.getOrderId());
        Assertions.assertEquals(domainEvent.getCourierId(), TestValues.EMPLOYEE_ID);
    }

    @Test
    public void test_deliver_order() {
        var order = TestDomainObjectsFactory.newPendingForDeliveringOrder();

        order.deliver();
        var domainEvent = getDomainEventByType(order, OrderDelivered.class);

        assertOrderDeliveredDomainEvent(domainEvent);
        AssertionUtil.assertCurrentDateTime(order.getDeliveredAt());
        Assertions.assertEquals(order.getStatus(), Order.Status.DELIVERED);
    }

    private void assertOrderDeliveredDomainEvent(OrderDelivered domainEvent) {
        var productsQuantitiesMap = domainEvent.getProductsQuantities();
        Assertions.assertNotNull(domainEvent);
        Assertions.assertNotNull(domainEvent.getOrderId());
        AssertionUtil.assertCurrentDateTime(domainEvent.getDeliveredAt());
        var productIds = productsQuantitiesMap.keySet();
        var productId = UUID.fromString(TestValues.FIRST_PRODUCT_ID);
        Assertions.assertTrue(productIds.contains(productId));
        Assertions.assertEquals(TestValues.FIRST_PRODUCT_QUANTITY, productsQuantitiesMap.get(productId));
    }

    private <T extends DomainEvent> T getDomainEventByType(Order order, Class<T> clazz) {
        return order.getDomainEvents().stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .findAny()
                .orElseThrow(() -> new RuntimeException(String.format("Domain event with type %s is not found",
                        clazz.getSimpleName())));
    }

    private <T extends DomainEvent> List<T> getDomainEventsByType(Order order, Class<T> clazz) {
        return order.getDomainEvents().stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .collect(Collectors.toList());
    }

}
