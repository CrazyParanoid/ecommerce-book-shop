package com.max.tech.ordering.util;

import com.max.tech.ordering.application.order.dto.OrderDTO;
import com.max.tech.ordering.domain.Address;
import com.max.tech.ordering.domain.Order;
import lombok.experimental.UtilityClass;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

@UtilityClass
public class AssertionUtil {

    public void assertCurrentDateTime(LocalDateTime date) {
        Assertions.assertTrue(date.toLocalDate().isEqual(LocalDate.now()));
    }

    public void assertAddress(Address address) {
        Assertions.assertEquals(address.getCity(), TestValues.CITY);
        Assertions.assertEquals(address.getStreet(), TestValues.STREET);
        Assertions.assertEquals(address.getHouse(), TestValues.HOUSE);
        Assertions.assertEquals(address.getFlat(), TestValues.FLAT);
        Assertions.assertEquals(address.getFloor(), TestValues.FLOOR);
        Assertions.assertEquals(address.getEntrance(), TestValues.ENTRANCE);
    }

    public void assertNewOrderDTO(OrderDTO orderDTO) {
        Assertions.assertNotNull(orderDTO.getOrderId());
        Assertions.assertEquals(orderDTO.getClientId(), TestValues.CLIENT_ID);
        Assertions.assertNull(orderDTO.getDeliveredAt());
        Assertions.assertEquals(orderDTO.getStatus(), Order.Status.PENDING_FOR_PRODUCTS.name());
        Assertions.assertNull(orderDTO.getCourierId());
        Assertions.assertEquals(orderDTO.getTotalPrice(), BigDecimal.ZERO.setScale(6, RoundingMode.UP));
        Assertions.assertTrue(orderDTO.getProducts().isEmpty());
        Assertions.assertEquals(orderDTO.getDeliveryAddress(), TestValues.FULlL_ADDRESS);
        Assertions.assertNull(orderDTO.getPaymentId());
    }

}
