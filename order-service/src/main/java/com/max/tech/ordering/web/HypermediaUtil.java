package com.max.tech.ordering.web;

import com.max.tech.ordering.application.dto.AddProductsToOrderCommand;
import com.max.tech.ordering.application.dto.OrderDTO;
import lombok.experimental.UtilityClass;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

@UtilityClass
public class HypermediaUtil {
    private final String PRODUCT_ID = "product_id";
    private final String PAYMENT_ID = "payment_id";

    public void addLinks(OrderDTO orderDTO) {
        addSelfLink(orderDTO);
        addPutProductLink(orderDTO);
        addPutPaymentLink(orderDTO);
        addDeleteProductLink(orderDTO);
        addDeleteProductsFromOrderLink(orderDTO);
        addFindOrderByClientIdLink(orderDTO);
        addPutDeliveryLink(orderDTO);
        addPatchDeliveryLink(orderDTO);
    }

    private void addSelfLink(OrderDTO orderDTO) {
        orderDTO.add(
                WebMvcLinkBuilder.linkTo(
                        WebMvcLinkBuilder.methodOn(OrderController.class)
                                .findOrderById(orderDTO.getOrderId()))
                        .withSelfRel());
    }

    private void addPutProductLink(OrderDTO orderDTO) {
        orderDTO.add(
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(OrderController.class)
                        .addProductToOrder(orderDTO.getOrderId(), new AddProductsToOrderCommand()))
                        .withRel("put-product")
        );
    }

    private void addPutPaymentLink(OrderDTO orderDTO) {
        orderDTO.add(
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(OrderController.class)
                                .confirmPayment(orderDTO.getOrderId(), PAYMENT_ID))
                        .withRel("put-payment")
        );
    }

    private void addDeleteProductLink(OrderDTO orderDTO) {
        orderDTO.add(
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(OrderController.class)
                        .removeProductFromOrder(orderDTO.getOrderId(), PRODUCT_ID))
                        .withRel("delete-product")
        );
    }

    private void addDeleteProductsFromOrderLink(OrderDTO orderDTO) {
        orderDTO.add(
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(OrderController.class)
                        .clearOrder(orderDTO.getOrderId()))
                        .withRel("delete-products-from-order")
        );
    }

    private void addFindOrderByClientIdLink(OrderDTO orderDTO) {
        orderDTO.add(
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(OrderController.class)
                        .findPendingProductsOrdersByClientId(orderDTO.getClientId()))
                        .withRel("get-order-by-client_id")
        );
    }

    private void addPutDeliveryLink(OrderDTO orderDTO) {
        orderDTO.add(
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(OrderController.class)
                        .takeOrderInDelivery(orderDTO.getOrderId()))
                        .withRel("put-delivery")
        );
    }

    private void addPatchDeliveryLink(OrderDTO orderDTO) {
        orderDTO.add(
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(OrderController.class)
                        .deliverOrder(orderDTO.getOrderId()))
                        .withRel("patch-delivery")
        );
    }

}
