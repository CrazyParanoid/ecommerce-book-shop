package com.max.tech.ordering.application;

import com.max.tech.ordering.application.dto.OrderDTO;
import com.max.tech.ordering.application.dto.OrderItemDTO;
import com.max.tech.ordering.domain.Order;
import com.max.tech.ordering.domain.item.OrderItem;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * See P of EAA by Martin Fowler ("Data Transfer Object" page 401)
 */
@Service
public class OrderAssembler {

    public OrderDTO writeDTO(Order order) {
        var orderDTO = new OrderDTO(
                order.getOrderId().toString(),
                order.getStatus().name(),
                order.getPersonId().toString(),
                order.getDeliveryAddressId().toString(),
                order.getDeliveredAt(),
                order.getTotalPrice().getValue()
        );

        Optional.ofNullable(order.getCourierId()).ifPresent(c -> orderDTO.setCourierId(c.toString()));
        Optional.ofNullable(order.getPaymentId()).ifPresent(p -> orderDTO.setPaymentId(p.toString()));

        writeItems(orderDTO, order.getItems());
        return orderDTO;
    }

    private void writeItems(OrderDTO orderDTO, Set<OrderItem> orderItems) {
        var itemDTOs = orderItems.stream().map(
                        p -> new OrderItemDTO(
                                p.itemId().toString(),
                                p.price().getValue(),
                                p.quantity()
                        )
                )
                .collect(Collectors.toSet());

        orderDTO.setItems(itemDTOs);
    }

}
