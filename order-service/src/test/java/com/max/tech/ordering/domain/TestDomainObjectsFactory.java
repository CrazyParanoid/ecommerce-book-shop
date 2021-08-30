package com.max.tech.ordering.domain;

import com.max.tech.ordering.domain.payment.PaymentId;
import com.max.tech.ordering.domain.item.OrderItem;
import com.max.tech.ordering.helper.TestValues;
import com.max.tech.ordering.domain.person.PersonId;
import com.max.tech.ordering.domain.item.OrderItemId;
import lombok.experimental.UtilityClass;

import java.util.Set;

@UtilityClass
public class TestDomainObjectsFactory {

    public Order newDeliveredOrder() {
        var order = newPendingForDeliveringOrder();
        order.deliver();
        return order;
    }

    public Order newOrder() {
        return Order.place(
                PersonId.fromValue(TestValues.CLIENT_ID),
                AddressId.fromValue(TestValues.ADDRESS_ID)
        );
    }

    public Order newOrderWithOneItem() {
        var order = newOrder();
        order.addItem(
                OrderItemId.fromValue(TestValues.FIRST_ITEM_ID),
                Amount.fromValue(TestValues.FIRST_ITEM_PRICE),
                TestValues.FIRST_ITEM_QUANTITY
        );

        return order;
    }

    public Order newOrderWithTwoItems() {
        var order = newOrder();
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

        return order;
    }

    public Order newOrderWithDiscount() {
        var order = newOrder();
        order.addItem(
                OrderItemId.fromValue(TestValues.FIRST_ITEM_ID),
                Amount.fromValue(TestValues.ITEM_PRICE_WITH_DISCOUNT),
                TestValues.FIRST_ITEM_QUANTITY
        );

        return order;
    }

    public Order newPendingDeliveryServiceOrder() {
        var order = newOrderWithOneItem();
        order.confirmPayment(new PaymentId(TestValues.PAYMENT_ID));
        return order;
    }

    public Order newPendingForDeliveringOrder() {
        var order = newPendingDeliveryServiceOrder();
        order.takeInDelivery(PersonId.fromValue(TestValues.EMPLOYEE_ID));
        return order;
    }

    public OrderPlaced raiseOrderPlacedDomainEvent() {
        return new OrderPlaced(
                OrderId.fromValue(TestValues.ORDER_ID),
                PersonId.fromValue(TestValues.CLIENT_ID),
                AddressId.fromValue(TestValues.ADDRESS_ID),
                Set.of(
                        new OrderItem(
                                OrderItemId.fromValue(TestValues.FIRST_ITEM_ID),
                                Amount.fromValue(TestValues.ITEM_PRICE_WITH_DISCOUNT),
                                TestValues.FIRST_ITEM_QUANTITY,
                                null
                        )
                )
        );
    }

}
