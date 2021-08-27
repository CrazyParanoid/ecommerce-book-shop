package com.max.tech.ordering.domain;

import com.max.tech.ordering.domain.payment.PaymentId;
import com.max.tech.ordering.util.TestValues;
import com.max.tech.ordering.domain.person.PersonId;
import com.max.tech.ordering.domain.product.ProductId;
import lombok.experimental.UtilityClass;

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

    public Order newOrderWithOneProduct() {
        var order = newOrder();
        order.addProduct(
                ProductId.fromValue(TestValues.FIRST_PRODUCT_ID),
                Amount.fromValue(TestValues.FIRST_PRODUCT_PRICE),
                TestValues.FIRST_PRODUCT_QUANTITY
        );

        return order;
    }

    public Order newOrderWithDiscount() {
        var order = newOrder();
        order.addProduct(
                ProductId.fromValue(TestValues.FIRST_PRODUCT_ID),
                Amount.fromValue(TestValues.PRODUCT_PRICE_WITH_DISCOUNT),
                TestValues.FIRST_PRODUCT_QUANTITY
        );

        return order;
    }

    public Order newPendingDeliveryServiceOrder() {
        var order = newOrderWithOneProduct();
        order.confirmPayment(new PaymentId(TestValues.PAYMENT_ID));
        return order;
    }

    public Order newPendingForDeliveringOrder() {
        var order = newPendingDeliveryServiceOrder();
        order.takeInDelivery(PersonId.fromValue(TestValues.EMPLOYEE_ID));
        return order;
    }

    public OrderPlaced raiseOrderCreatedDomainEvent() {
        return new OrderPlaced(
                OrderId.fromValue(TestValues.ORDER_ID),
                PersonId.fromValue(TestValues.CLIENT_ID),
                AddressId.fromValue(TestValues.ADDRESS_ID)
        );
    }

}
