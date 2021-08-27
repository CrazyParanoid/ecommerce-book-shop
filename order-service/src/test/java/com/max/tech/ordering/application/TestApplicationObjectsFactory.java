package com.max.tech.ordering.application;

import com.max.tech.ordering.application.dto.*;
import com.max.tech.ordering.domain.Order;
import com.max.tech.ordering.util.TestValues;
import lombok.experimental.UtilityClass;

import java.util.Collections;
import java.util.Set;

@UtilityClass
public class TestApplicationObjectsFactory {

    public PlaceOrderCommand newPlaceOrderCommand() {
        return new PlaceOrderCommand(
                TestValues.CLIENT_ID,
                TestValues.ADDRESS_ID,
                Collections.singletonList(
                        new ProductDTO(
                                TestValues.FIRST_PRODUCT_ID,
                                TestValues.FIRST_PRODUCT_PRICE,
                                TestValues.FIRST_PRODUCT_QUANTITY
                        )
                )
        );
    }

    public TakeOrderToDeliveryCommand newTakeOrderToDeliveryCommand() {
        return new TakeOrderToDeliveryCommand(
                TestValues.ORDER_ID,
                TestValues.EMPLOYEE_ID
        );
    }

    public OrderDTO newOrderDTO() {
        return new OrderDTO(
                TestValues.ORDER_ID,
                Order.Status.PENDING_PAYMENT.name(),
                TestValues.CLIENT_ID,
                TestValues.EMPLOYEE_ID,
                null,
                TestValues.ORDER_TOTAL_PRICE,
                null,
                TestValues.ADDRESS_ID,
                Set.of(
                        new ProductDTO(
                                TestValues.FIRST_PRODUCT_ID,
                                TestValues.FIRST_PRODUCT_PRICE,
                                TestValues.FIRST_PRODUCT_QUANTITY
                        )
                )
        );
    }

}
