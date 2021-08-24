package com.max.tech.ordering.application;

import com.max.tech.ordering.application.client.dto.AddressDTO;
import com.max.tech.ordering.application.client.dto.ClientDTO;
import com.max.tech.ordering.application.client.dto.RegisterNewClientCommand;
import com.max.tech.ordering.application.order.dto.*;
import com.max.tech.ordering.domain.Order;
import com.max.tech.ordering.util.TestValues;
import lombok.experimental.UtilityClass;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@UtilityClass
public class TestApplicationObjectsFactory {

    public CreateNewOrderCommand newCreateNewOrderCommand() {
        return new CreateNewOrderCommand(
                TestValues.CLIENT_ID,
                Collections.singletonList(
                        new ProductDTO(
                                TestValues.FIRST_PRODUCT_ID,
                                TestValues.FIRST_PRODUCT_PRICE,
                                TestValues.FIRST_PRODUCT_QUANTITY
                        )
                )
        );
    }

    public AddProductsToOrderCommand newAddProductToOrderCommand() {
        return new AddProductsToOrderCommand(
                TestValues.ORDER_ID,
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

    public OrderDTO newOrderDTOWithProducts() {
        var orderDTO = newOrderDTO();
        orderDTO.setProducts(
                Set.of(new ProductDTO(
                                TestValues.FIRST_PRODUCT_ID,
                                TestValues.FIRST_PRODUCT_PRICE,
                                TestValues.FIRST_PRODUCT_QUANTITY
                        ),
                        new ProductDTO(
                                TestValues.SECOND_PRODUCT_ID,
                                TestValues.SECOND_PRODUCT_PRICE,
                                TestValues.SECOND_PRODUCT_QUANTITY
                        ))
        );
        return orderDTO;
    }

    public OrderDTO newOrderDTO() {
        return new OrderDTO(
                TestValues.ORDER_ID,
                Order.Status.PENDING_FOR_PRODUCTS.name(),
                TestValues.CLIENT_ID,
                TestValues.EMPLOYEE_ID,
                null,
                TestValues.ORDER_TOTAL_PRICE,
                TestValues.FULlL_ADDRESS,
                null,
                new HashSet<>()
        );
    }

    public RegisterNewClientCommand newRegisterNewClientCommand() {
        return new RegisterNewClientCommand(
                TestValues.CLIENT_ID,
                new AddressDTO(
                        TestValues.CITY,
                        TestValues.STREET,
                        TestValues.HOUSE,
                        TestValues.FLAT,
                        TestValues.FLOOR,
                        TestValues.ENTRANCE
                )
        );
    }

    public ClientDTO newClientDTO() {
        return new ClientDTO(
                TestValues.CLIENT_ID,
                new AddressDTO(
                        TestValues.CITY,
                        TestValues.STREET,
                        TestValues.HOUSE,
                        TestValues.FLAT,
                        TestValues.FLOOR,
                        TestValues.ENTRANCE
                )
        );
    }

}
