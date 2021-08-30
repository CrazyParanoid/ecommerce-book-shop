package com.max.tech.ordering.web;

import com.max.tech.ordering.application.dto.AddItemToOrderCommand;
import com.max.tech.ordering.application.dto.OrderDTO;
import lombok.experimental.UtilityClass;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

@UtilityClass
public class HypermediaUtil {
    private final String ITEM_ID = "item_id";
    private final String PAYMENT_ID = "payment_id";

    public void addLinks(OrderDTO orderDTO) {
        addSelfLink(orderDTO);
        addPutPaymentLink(orderDTO);
        addPutItemLink(orderDTO);
        addDeleteItemLink(orderDTO);
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

    private void addPutPaymentLink(OrderDTO orderDTO) {
        orderDTO.add(
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(OrderController.class)
                                .confirmPayment(orderDTO.getOrderId(), PAYMENT_ID))
                        .withRel("put-payment")
        );
    }

    private void addPutItemLink(OrderDTO orderDTO) {
        orderDTO.add(
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(OrderController.class)
                                .addItemToOrder(orderDTO.getOrderId(), new AddItemToOrderCommand()))
                        .withRel("put-item")
        );
    }

    private void addDeleteItemLink(OrderDTO orderDTO) {
        orderDTO.add(
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(OrderController.class)
                                .removeItemFromOrder(orderDTO.getOrderId(), ITEM_ID))
                        .withRel("delete-item")
        );
    }

    private void addFindOrderByClientIdLink(OrderDTO orderDTO) {
        orderDTO.add(
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(OrderController.class)
                                .findPendingPaymentOrdersByClientId(orderDTO.getClientId()))
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
