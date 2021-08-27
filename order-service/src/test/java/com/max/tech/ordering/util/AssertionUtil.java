package com.max.tech.ordering.util;

import com.max.tech.ordering.application.dto.OrderDTO;
import com.max.tech.ordering.domain.Order;
import lombok.experimental.UtilityClass;
import org.junit.jupiter.api.Assertions;

import java.time.LocalDate;
import java.time.LocalDateTime;

@UtilityClass
public class AssertionUtil {

    public void assertCurrentDateTime(LocalDateTime date) {
        Assertions.assertTrue(date.toLocalDate().isEqual(LocalDate.now()));
    }

    public void assertOrderDTO(OrderDTO orderDTO) {
        Assertions.assertNotNull(orderDTO.getOrderId());
        Assertions.assertEquals(orderDTO.getClientId(), TestValues.CLIENT_ID);
        Assertions.assertNull(orderDTO.getDeliveredAt());
        Assertions.assertEquals(orderDTO.getStatus(), Order.Status.PENDING_PAYMENT.name());
        Assertions.assertNull(orderDTO.getCourierId());
        Assertions.assertEquals(orderDTO.getTotalPrice(), TestValues.TOTAL_ORDER_PRICE_WITH_ONE_PRODUCT);
        Assertions.assertFalse(orderDTO.getProducts().isEmpty());
        Assertions.assertNull(orderDTO.getPaymentId());
        Assertions.assertEquals(orderDTO.getDeliveryAddressId(), TestValues.ADDRESS_ID);

        var product = orderDTO.getProducts()
                .stream()
                .findFirst()
                .orElse(null);

        Assertions.assertNotNull(product);
        Assertions.assertEquals(product.getProductId(), TestValues.FIRST_PRODUCT_ID);
        Assertions.assertEquals(product.getPrice(), TestValues.FIRST_PRODUCT_PRICE);
        Assertions.assertEquals(product.getQuantity(), TestValues.FIRST_PRODUCT_QUANTITY);
    }

}
