package com.max.tech.ordering.web;

import com.max.tech.ordering.application.client.dto.ClientDTO;
import com.max.tech.ordering.application.order.dto.AddProductToOrderCommand;
import com.max.tech.ordering.application.order.dto.OrderDTO;
import lombok.experimental.UtilityClass;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

@UtilityClass
public class HypermediaUtil {
    private final String PRODUCT_ID = "product_id";

    public void addLinks(ClientDTO clientDTO) {
        addSelfLink(clientDTO);
        addDeleteLink(clientDTO);
    }

    private void addSelfLink(ClientDTO clientDTO) {
        clientDTO.add(
                WebMvcLinkBuilder.linkTo(
                        WebMvcLinkBuilder.methodOn(ClientController.class)
                                .findClientById(clientDTO.getClientId()))
                        .withSelfRel());
    }

    private void addDeleteLink(ClientDTO clientDTO) {
        clientDTO.add(
                WebMvcLinkBuilder.linkTo(
                        WebMvcLinkBuilder.methodOn(ClientController.class)
                                .deleteClient(clientDTO.getClientId()))
                        .withRel("delete-client"));
    }

    public void addLinks(OrderDTO orderDTO) {
        addSelfLink(orderDTO);
        addPutProductLink(orderDTO);
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
                        .addProductToOrder(orderDTO.getOrderId(), new AddProductToOrderCommand()))
                        .withRel("put-product")
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
                        .takeOrderInDelivery(orderDTO.getClientId()))
                        .withRel("put-delivery")
        );
    }

    private void addPatchDeliveryLink(OrderDTO orderDTO) {
        orderDTO.add(
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(OrderController.class)
                        .deliverOrder(orderDTO.getClientId()))
                        .withRel("patch-delivery")
        );
    }

}
