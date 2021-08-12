package com.max.tech.ordering.application.order;

import com.max.tech.ordering.application.order.dto.OrderDTO;
import com.max.tech.ordering.application.order.dto.ProductDTO;
import com.max.tech.ordering.domain.Order;
import com.max.tech.ordering.domain.product.Product;
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
                order.getClientId().toString(),
                order.getDeliveredAt(),
                order.getDeliveryAddress().toString(),
                order.getTotalPrice().getValue()
        );

        Optional.ofNullable(order.getCourierId()).ifPresent(c -> orderDTO.setCourierId(c.toString()));
        Optional.ofNullable(order.getPaymentId()).ifPresent(p -> orderDTO.setPaymentId(p.toString()));

        writeProducts(orderDTO, order.getProducts());
        return orderDTO;
    }

    private void writeProducts(OrderDTO orderDTO, Set<Product> products) {
        var productDTOs = products.stream().map(
                p -> new ProductDTO(
                        p.getProductId().toString(),
                        p.getPrice().getValue(),
                        p.getQuantity()
                )
        )
                .collect(Collectors.toSet());

        orderDTO.setProducts(productDTOs);
    }

}
