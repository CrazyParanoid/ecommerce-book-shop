package com.max.tech.ordering.domain;

import com.max.tech.ordering.domain.payment.PaymentId;
import com.max.tech.ordering.util.TestValues;
import com.max.tech.ordering.domain.client.Client;
import com.max.tech.ordering.domain.client.ClientId;
import com.max.tech.ordering.domain.employee.EmployeeId;
import com.max.tech.ordering.domain.product.ProductId;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TestDomainObjectsFactory {

    public Order newDeliveredOrder() {
        var order = newPendingForDeliveringOrder();
        order.deliver();
        return order;
    }

    public Client newClient() {
        return Client.newBuilder()
                .withId(TestValues.CLIENT_ID)
                .withAddress(
                        TestValues.CITY,
                        TestValues.STREET,
                        TestValues.HOUSE,
                        TestValues.FLAT,
                        TestValues.FLOOR,
                        TestValues.ENTRANCE
                )
                .build();
    }

    public Order newOrder() {
        return Order.newOrder(
                ClientId.fromValue(TestValues.CLIENT_ID),
                newAddress()
        );
    }

    private Address newAddress() {
        return new Address(
                TestValues.CITY,
                TestValues.STREET,
                TestValues.HOUSE,
                TestValues.FLAT,
                TestValues.FLOOR,
                TestValues.ENTRANCE
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
        order.takeInDelivery(EmployeeId.fromValue(TestValues.EMPLOYEE_ID));
        return order;
    }

    public OrderCreated raiseOrderCreatedDomainEvent() {
        return new OrderCreated(
                OrderId.fromValue(TestValues.ORDER_ID),
                ClientId.fromValue(TestValues.CLIENT_ID),
                newAddress()
        );
    }

}
