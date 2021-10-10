package com.max.tech.ordering.web;

import com.max.tech.ordering.application.OrderService;
import com.max.tech.ordering.application.dto.AddItemToOrderCommand;
import com.max.tech.ordering.application.dto.OrderDTO;
import com.max.tech.ordering.application.dto.PlaceOrderCommand;
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
    @ApiOperation(value = "Place order")
    public OrderDTO placeOrder(@RequestBody @Valid PlaceOrderCommand command) {
        command.setClientId(extractClientId());
        var oderDTO = orderService.placeOrder(command);
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
    @ApiOperation(value = "Find pending payment orders for client")
    public List<OrderDTO> findPendingPaymentOrdersByClientId(@RequestParam("client_id") String clientId) {
        return orderService.findPendingPaymentOrders(clientId);
    }

    @PostMapping(value = "/{orderId}/payment/{paymentId}")
    @ApiOperation(value = "Confirm order payment")
    public ResponseEntity<Void> confirmPayment(@PathVariable String orderId, @PathVariable String paymentId) {
        orderService.confirmOrderPayment(orderId, paymentId);
        return ResponseEntity.noContent().build();
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/{orderId}/items")
    @ApiOperation(value = "Add item to order")
    public OrderDTO addItemToOrder(@PathVariable String orderId,
                                   @RequestBody @Valid AddItemToOrderCommand command) {
        command.setOrderId(orderId);
        var orderDTO = orderService.addItemToOrder(command);
        HypermediaUtil.addLinks(orderDTO);
        return orderDTO;
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "/{orderId}/items/{itemId}")
    @ApiOperation(value = "Remove item from order")
    public OrderDTO removeItemFromOrder(@PathVariable String orderId,
                                        @PathVariable String itemId) {
        var orderDTO = orderService.removeItemFromOrder(orderId, itemId);
        HypermediaUtil.addLinks(orderDTO);
        return orderDTO;
    }

    @PostMapping(value = "/{orderId}/courier")
    @ApiOperation(value = "Assign courier to order")
    public ResponseEntity<Void> assignCourierToOrder(@PathVariable String orderId) {
        orderService.assignCourierToOrder(orderId, extractClientId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{orderId}/delivery")
    @ApiOperation(value = "Deliver order")
    public ResponseEntity<Void> deliverOrder(@PathVariable String orderId) {
        orderService.deliverOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    private String extractClientId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var user = (User) authentication.getPrincipal();
        return user.id();
    }

}
