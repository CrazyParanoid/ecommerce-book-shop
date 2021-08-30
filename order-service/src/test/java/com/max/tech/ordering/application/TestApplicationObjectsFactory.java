package com.max.tech.ordering.application;

import com.max.tech.ordering.application.dto.*;
import com.max.tech.ordering.domain.Order;
import com.max.tech.ordering.helper.TestValues;
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
                        new OrderItemDTO(
                                TestValues.FIRST_ITEM_ID,
                                TestValues.FIRST_ITEM_PRICE,
                                TestValues.FIRST_ITEM_QUANTITY
                        )
                )
        );
    }

    public AddItemToOrderCommand newAddItemToOrderCommand() {
        return new AddItemToOrderCommand(
                TestValues.ORDER_ID,
                TestValues.SECOND_ITEM_ID,
                TestValues.SECOND_ITEM_PRICE,
                TestValues.SECOND_ITEM_QUANTITY
        );
    }

    public OrderDTO newOrderDTOWithOneItem() {
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
                        new OrderItemDTO(
                                TestValues.FIRST_ITEM_ID,
                                TestValues.FIRST_ITEM_PRICE,
                                TestValues.FIRST_ITEM_QUANTITY
                        )
                )
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
                        new OrderItemDTO(
                                TestValues.FIRST_ITEM_ID,
                                TestValues.FIRST_ITEM_PRICE,
                                TestValues.FIRST_ITEM_QUANTITY
                        ),
                        new OrderItemDTO(
                                TestValues.SECOND_ITEM_ID,
                                TestValues.SECOND_ITEM_PRICE,
                                TestValues.SECOND_ITEM_QUANTITY
                        )
                )
        );
    }

}
