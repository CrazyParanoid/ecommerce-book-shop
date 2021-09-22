package com.max.tech.ordering.domain;

import com.max.tech.ordering.domain.common.DomainEvent;
import com.max.tech.ordering.domain.item.OrderItemId;
import com.max.tech.ordering.domain.payment.PaymentId;
import com.max.tech.ordering.domain.person.PersonId;
import com.max.tech.ordering.helper.TestValues;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class OrderTest {

    @Test
    public void shouldPlaceOrder() {
        var order = TestDomainObjectsFactory.newOrder();
        var orderPlacedDomainEvent = getDomainEventByType(order, OrderPlaced.class);

        var items = order.getItems();
        Assertions.assertNotNull(items);
        Assertions.assertNull(order.getPaymentId());
        Assertions.assertNull(order.getCourierId());
        Assertions.assertNull(order.getDeliveredAt());
        Assertions.assertTrue(items.isEmpty());
        Assertions.assertEquals(order.getStatus(), Order.Status.PENDING_PAYMENT);
        Assertions.assertNotNull(order.getOrderId());
        Assertions.assertEquals(order.getPersonId().toString(), TestValues.CLIENT_ID);
        Assertions.assertEquals(order.getTotalPrice(), Amount.ZERO_AMOUNT);
        Assertions.assertEquals(order.getDeliveryAddressId().toString(), TestValues.ADDRESS_ID);
        Assertions.assertNotNull(orderPlacedDomainEvent.getOrderId());
        Assertions.assertEquals(orderPlacedDomainEvent.getClientId(), TestValues.CLIENT_ID);
        Assertions.assertEquals(orderPlacedDomainEvent.getDeliveryAddressId(), TestValues.ADDRESS_ID);
    }

    @Test
    public void shouldAddItemToOrder() {
        var order = TestDomainObjectsFactory.newOrder();

        order.addItem(
                OrderItemId.fromValue(TestValues.FIRST_ITEM_ID),
                Amount.fromValue(TestValues.FIRST_ITEM_PRICE),
                TestValues.FIRST_ITEM_QUANTITY
        );

        var domainEvent = getDomainEventByType(order, OrderItemAdded.class);
        var item = order.findItemById(OrderItemId.fromValue(TestValues.FIRST_ITEM_ID));
        Assertions.assertNotNull(item);
        Assertions.assertEquals(item.itemId().toString(), TestValues.FIRST_ITEM_ID);
        Assertions.assertEquals(item.price().getValue(), TestValues.FIRST_ITEM_PRICE);
        Assertions.assertEquals(item.quantity(), TestValues.FIRST_ITEM_QUANTITY);
        Assertions.assertEquals(domainEvent.getItemId(), TestValues.FIRST_ITEM_ID);
        Assertions.assertEquals(domainEvent.getTotalPrice(), TestValues.TOTAL_ORDER_PRICE_WITH_ONE_ITEM);
        Assertions.assertEquals(domainEvent.getQuantity(), TestValues.FIRST_ITEM_QUANTITY);
        Assertions.assertNotNull(domainEvent.getOrderId());
        Assertions.assertEquals(order.getTotalPrice().getValue(), TestValues.TOTAL_ORDER_PRICE_WITH_ONE_ITEM);
    }

    @Test
    public void shouldThrowExceptionDuringAddingItemWithZeroQuantity() {
        var order = TestDomainObjectsFactory.newOrder();

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> order.addItem(
                        OrderItemId.fromValue(TestValues.FIRST_ITEM_ID),
                        Amount.fromValue(TestValues.FIRST_ITEM_PRICE),
                        0
                ),
                "Illegal quantity 0. Quantity must be greater than 0");
    }

    @Test
    public void shouldThrowExceptionDuringAddingItemWithZeroPrice() {
        var order = TestDomainObjectsFactory.newOrder();

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> order.addItem(
                        OrderItemId.fromValue(TestValues.FIRST_ITEM_ID),
                        Amount.ZERO_AMOUNT,
                        TestValues.FIRST_ITEM_QUANTITY
                ),
                "Illegal price 0. Price must be greater than 0");
    }

    @Test
    public void shouldAddTwoItemsToOrder() {
        var order = TestDomainObjectsFactory.newOrder();

        order.addItem(
                OrderItemId.fromValue(TestValues.FIRST_ITEM_ID),
                Amount.fromValue(TestValues.FIRST_ITEM_PRICE),
                TestValues.FIRST_ITEM_QUANTITY
        );
        order.addItem(
                OrderItemId.fromValue(TestValues.SECOND_ITEM_ID),
                Amount.fromValue(TestValues.SECOND_ITEM_PRICE),
                TestValues.SECOND_ITEM_QUANTITY
        );

        var domainEvents = getDomainEventsByType(order, OrderItemAdded.class);

        var firstItem = order.findItemById(OrderItemId.fromValue(TestValues.FIRST_ITEM_ID));
        Assertions.assertNotNull(firstItem);
        Assertions.assertEquals(firstItem.itemId().toString(), TestValues.FIRST_ITEM_ID);
        Assertions.assertEquals(firstItem.price().getValue(), TestValues.FIRST_ITEM_PRICE);
        Assertions.assertEquals(firstItem.quantity(), TestValues.FIRST_ITEM_QUANTITY);

        var secondItem = order.findItemById(OrderItemId.fromValue(TestValues.SECOND_ITEM_ID));
        Assertions.assertEquals(secondItem.itemId().toString(), TestValues.SECOND_ITEM_ID);
        Assertions.assertEquals(secondItem.price().getValue(), TestValues.SECOND_ITEM_PRICE);
        Assertions.assertEquals(secondItem.quantity(), TestValues.SECOND_ITEM_QUANTITY);

        Assertions.assertEquals(domainEvents.get(0).getItemId(), TestValues.FIRST_ITEM_ID);
        Assertions.assertEquals(domainEvents.get(0).getTotalPrice(), TestValues.TOTAL_ORDER_PRICE_WITH_ONE_ITEM);
        Assertions.assertEquals(domainEvents.get(0).getQuantity(), TestValues.FIRST_ITEM_QUANTITY);
        Assertions.assertNotNull(domainEvents.get(0).getOrderId());
        Assertions.assertEquals(domainEvents.get(1).getItemId(), TestValues.SECOND_ITEM_ID);
        Assertions.assertEquals(domainEvents.get(1).getTotalPrice(), TestValues.TOTAL_ORDER_PRICE_WITH_TWO_ITEMS);
        Assertions.assertEquals(domainEvents.get(1).getQuantity(), TestValues.SECOND_ITEM_QUANTITY);
        Assertions.assertNotNull(domainEvents.get(1).getOrderId());
        Assertions.assertEquals(order.getTotalPrice().getValue(), TestValues.TOTAL_ORDER_PRICE_WITH_TWO_ITEMS);
    }

    @Test
    public void shouldThrowExceptionDuringAddingItemIfOrderDelivered() {
        var order = TestDomainObjectsFactory.newDeliveredOrder();

        Assertions.assertThrows(IllegalStateException.class,
                () -> order.addItem(
                        OrderItemId.fromValue(TestValues.FIRST_ITEM_ID),
                        Amount.fromValue(TestValues.FIRST_ITEM_PRICE),
                        TestValues.FIRST_ITEM_QUANTITY
                ),
                "Wrong invocation for current state: expected PENDING_PAYMENT, but actual DELIVERED");
    }

    @Test
    public void shouldRemoveItemFromOrder() {
        var order = TestDomainObjectsFactory.newOrderWithTwoItems();

        order.removeItem(OrderItemId.fromValue(TestValues.SECOND_ITEM_ID));
        var domainEvent = getDomainEventByType(order, OrderItemRemoved.class);

        Assertions.assertNotNull(domainEvent.getOrderId());
        Assertions.assertEquals(domainEvent.getItemId(), TestValues.SECOND_ITEM_ID);
        Assertions.assertEquals(domainEvent.getTotalPrice(), TestValues.TOTAL_ORDER_PRICE_WITH_ONE_ITEM);
        Assertions.assertEquals(order.getTotalPrice().getValue(), TestValues.TOTAL_ORDER_PRICE_WITH_ONE_ITEM);
    }

    @Test
    public void shouldThrowExceptionDuringRemovingItemIfItemsEmpty() {
        var order = TestDomainObjectsFactory.newOrder();

        Assertions.assertThrows(IllegalStateException.class,
                () -> order.removeItem(OrderItemId.fromValue(TestValues.FIRST_ITEM_ID)),
                "Item with id 7417d778-fabe-4a90-ad93-3dfb74c51608 can't be deleted. " +
                        "Order items can't be empty");
    }

    @Test
    public void shouldConfirmPayment() {
        var order = TestDomainObjectsFactory.newOrderWithOneItem();

        order.confirmPayment(new PaymentId(TestValues.PAYMENT_ID));
        var domainEvent = getDomainEventByType(order, OrderPaid.class);

        Assertions.assertEquals(order.getStatus(), Order.Status.PENDING_COURIER_ASSIGMENT);
        Assertions.assertNotNull(domainEvent);
        Assertions.assertNotNull(domainEvent.getOrderId());
        Assertions.assertEquals(domainEvent.getPaymentId().toString(), TestValues.PAYMENT_ID);
    }

    @Test
    public void shouldThrowExceptionDuringPaymentConfirmationIfItemsEmpty() {
        var order = TestDomainObjectsFactory.newOrder();

        Assertions.assertThrows(IllegalStateException.class,
                () -> order.confirmPayment(new PaymentId(TestValues.PAYMENT_ID)),
                "Payment can't be confirmed. Order items can't be empty");
    }

    @Test
    public void shouldAssignCourier() {
        var order = TestDomainObjectsFactory.newPendingDeliveryServiceOrder();

        order.assignCourier(PersonId.fromValue(TestValues.EMPLOYEE_ID));
        var domainEvent = getDomainEventByType(order, OrderCourierAssigned.class);

        Assertions.assertEquals(order.getStatus(), Order.Status.PENDING_FOR_DELIVERING);
        Assertions.assertEquals(order.getCourierId().toString(), TestValues.EMPLOYEE_ID);
        Assertions.assertNotNull(domainEvent);
        Assertions.assertNotNull(domainEvent.getOrderId());
        Assertions.assertEquals(domainEvent.getCourierId(), TestValues.EMPLOYEE_ID);
    }

    @Test
    public void shouldDeliverOrder() {
        var order = TestDomainObjectsFactory.newPendingForDeliveringOrder();

        order.deliver();
        var domainEvent = getDomainEventByType(order, OrderDelivered.class);

        Assertions.assertNotNull(domainEvent);
        Assertions.assertNotNull(domainEvent.getOrderId());
        Assertions.assertTrue(domainEvent.getDeliveredAt().toLocalDate().isEqual(LocalDate.now()));
        Assertions.assertTrue(order.getDeliveredAt().toLocalDate().isEqual(LocalDate.now()));
        Assertions.assertEquals(order.getStatus(), Order.Status.DELIVERED);
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
