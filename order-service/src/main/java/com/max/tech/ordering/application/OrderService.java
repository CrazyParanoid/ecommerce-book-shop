package com.max.tech.ordering.application;

import com.max.tech.ordering.application.dto.AddItemToOrderCommand;
import com.max.tech.ordering.application.dto.OrderDTO;
import com.max.tech.ordering.application.dto.PlaceOrderCommand;
import com.max.tech.ordering.domain.*;
import com.max.tech.ordering.domain.common.DomainEventPublisher;
import com.max.tech.ordering.domain.payment.PaymentId;
import com.max.tech.ordering.domain.person.PersonId;
import com.max.tech.ordering.domain.item.OrderItemId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final DomainEventPublisher domainEventPublisher;
    private final OrderAssembler orderAssembler;

    @Autowired
    public OrderService(OrderRepository orderRepository, DomainEventPublisher domainEventPublisher,
                        OrderAssembler orderAssembler) {
        this.orderRepository = orderRepository;
        this.domainEventPublisher = domainEventPublisher;
        this.orderAssembler = orderAssembler;
    }

    /**
     * Place order with items, if they have been selected.
     *
     * @param command request to place an order
     * @return created order
     */
    @Transactional
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('BUYER')")
    public OrderDTO placeOrder(PlaceOrderCommand command) {
        var order = Order.place(
                PersonId.fromValue(command.getClientId()),
                AddressId.fromValue(command.getDeliveryAddressId())
        );
        command.getItems().forEach(
                item -> order.addItem(
                        OrderItemId.fromValue(item.getItemId()),
                        Amount.fromValue(item.getPrice()),
                        item.getQuantity()
                )
        );

        domainEventPublisher.publish(order.getDomainEvents());

        orderRepository.save(order);
        log.info("Order with id {} has been created", order.getOrderId().toString());
        return orderAssembler.writeDTO(order);
    }

    @Transactional
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('BUYER')")
    public OrderDTO removeItemFromOrder(String orderId, String itemId) {
        var order = orderRepository.findOrderById(OrderId.fromValue(orderId))
                .orElseThrow(() -> new OrderNotFoundException(String.format("Order with id %s is not found", orderId)));

        order.removeItem(OrderItemId.fromValue(itemId));

        domainEventPublisher.publish(order.getDomainEvents());
        orderRepository.save(order);

        log.info("Item with id {} has been removed from order, id {}", itemId, orderId);
        return orderAssembler.writeDTO(order);
    }

    @Transactional
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('BUYER')")
    public OrderDTO addItemToOrder(AddItemToOrderCommand command) {
        var order = orderRepository.findOrderById(OrderId.fromValue(command.getOrderId()))
                .orElseThrow(() -> new OrderNotFoundException(String.format("Order with id %s is not found", command.getOrderId())));

        order.addItem(
                OrderItemId.fromValue(command.getItemId()),
                Amount.fromValue(command.getPrice()),
                command.getQuantity()
        );

        domainEventPublisher.publish(order.getDomainEvents());
        orderRepository.save(order);

        log.info("Item with id {} has been added to order, id {}", command.getItemId(), command.getOrderId());
        return orderAssembler.writeDTO(order);
    }

    @Transactional
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('BUYER')")
    public void confirmOrderPayment(String orderId, String paymentId) {
        var order = orderRepository.findOrderById(OrderId.fromValue(orderId))
                .orElseThrow(() -> new OrderNotFoundException(String.format("Order with id %s is not found", orderId)));

        order.confirmPayment(new PaymentId(paymentId));

        domainEventPublisher.publish(order.getDomainEvents());
        orderRepository.save(order);

        log.info("Payment for order with id {} has been confirmed", orderId);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('COURIER')")
    public void takeOrderToDelivery(String anOrderId, String aCourierId) {
        var order = orderRepository.findOrderById(OrderId.fromValue(anOrderId))
                .orElseThrow(() -> new OrderNotFoundException(String.format("Order with id %s is not found", anOrderId)));

        order.takeInDelivery(PersonId.fromValue(aCourierId));

        domainEventPublisher.publish(order.getDomainEvents());
        orderRepository.save(order);

        log.info("Order with id {} has been sent to delivery service", anOrderId);
    }

    @Transactional
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('COURIER')")
    public void deliverOrder(String orderId) {
        var order = orderRepository.findOrderById(OrderId.fromValue(orderId))
                .orElseThrow(() -> new OrderNotFoundException(String.format("Order with id %s is not found", orderId)));

        order.deliver();

        domainEventPublisher.publish(order.getDomainEvents());
        orderRepository.save(order);

        log.info("Order with id {} has been delivered", orderId);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('COURIER')")
    public OrderDTO findOrderById(String orderId) {
        return orderRepository.findOrderById(OrderId.fromValue(orderId))
                .map(orderAssembler::writeDTO)
                .orElseThrow(() -> new OrderNotFoundException(String.format("Order with id %s is not found", orderId)));
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('BUYER')")
    public List<OrderDTO> findPendingPaymentOrders(String clientId) {
        return orderRepository.findPendingPaymentOrdersForClient(PersonId.fromValue(clientId))
                .stream()
                .map(orderAssembler::writeDTO)
                .collect(Collectors.toList());
    }

}
