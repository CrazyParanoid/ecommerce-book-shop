package com.max.tech.ordering.web;

import com.max.tech.ordering.application.order.OrderService;
import com.max.tech.ordering.application.order.dto.AddProductToOrderCommand;
import com.max.tech.ordering.application.order.dto.OrderDTO;
import com.max.tech.ordering.application.order.dto.TakeOrderToDeliveryCommand;
import com.max.tech.ordering.web.security.User;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "order", description = "The ordering REST API")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Create new order")
    public OrderDTO createNewOrder() {
        var oderDTO = orderService.createNewOrder(extractClientId());
        HypermediaUtil.addLinks(oderDTO);
        return oderDTO;
    }

    @GetMapping(value = "/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Find order by id")
    public OrderDTO findOrderById(@PathVariable String orderId) {
        var orderDTO = orderService.findOrderById(orderId);
        HypermediaUtil.addLinks(orderDTO);
        return orderDTO;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Find pending products orders for client")
    public List<OrderDTO> findPendingProductsOrdersByClientId(@RequestParam("client_id") String clientId) {
        return orderService.findPendingProductsOrders(clientId);
    }

    @PutMapping(value = "/{orderId}/products")
    @ApiOperation(value = "Add product to order")
    public ResponseEntity<Void> addProductToOrder(@PathVariable String orderId,
                                                  @RequestBody @Valid AddProductToOrderCommand command) {
        command.setOrderId(orderId);
        orderService.addProductToOrder(command);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/{orderId}/products/{productId}")
    @ApiOperation(value = "Remove product from order")
    public ResponseEntity<Void> removeProductFromOrder(@PathVariable String orderId,
                                                       @PathVariable String productId) {
        orderService.removeProductFromOrder(orderId, productId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/{orderId}")
    @ApiOperation(value = "Remove all products from order")
    public ResponseEntity<Void> clearOrder(@PathVariable String orderId) {
        orderService.clearOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{orderId}/delivery")
    @ApiOperation(value = "Take order in delivery")
    public ResponseEntity<Void> takeOrderInDelivery(@PathVariable String orderId) {
        orderService.takeOrderToDelivery(
                new TakeOrderToDeliveryCommand(orderId, extractClientId())
        );
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/{orderId}/delivery")
    @ApiOperation(value = "Deliver order")
    public ResponseEntity<Void> deliverOrder(@PathVariable String orderId) {
        orderService.deliverOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    private String extractClientId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var user = (User) authentication.getPrincipal();
        return user.getId();
    }

}
