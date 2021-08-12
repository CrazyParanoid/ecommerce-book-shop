package com.max.tech.ordering.application.order;

import com.max.tech.ordering.application.order.dto.AddProductToOrderCommand;
import com.max.tech.ordering.application.order.dto.OrderDTO;
import com.max.tech.ordering.application.order.dto.TakeOrderToDeliveryCommand;
import com.max.tech.ordering.domain.Amount;
import com.max.tech.ordering.domain.Order;
import com.max.tech.ordering.domain.OrderId;
import com.max.tech.ordering.domain.OrderRepository;
import com.max.tech.ordering.domain.client.ClientId;
import com.max.tech.ordering.domain.client.ClientRepository;
import com.max.tech.ordering.domain.common.DomainEventPublisher;
import com.max.tech.ordering.domain.employee.EmployeeId;
import com.max.tech.ordering.domain.payment.PaymentId;
import com.max.tech.ordering.domain.product.ProductId;
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
    private final ClientRepository clientRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, DomainEventPublisher domainEventPublisher,
                        OrderAssembler orderAssembler, ClientRepository clientRepository) {
        this.orderRepository = orderRepository;
        this.domainEventPublisher = domainEventPublisher;
        this.orderAssembler = orderAssembler;
        this.clientRepository = clientRepository;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('BUYER')")
    public OrderDTO createNewOrder(String aClientId) {
        var existedOrders = orderRepository.findPendingProductsOrdersForClient(ClientId.fromValue(aClientId));
        if (!existedOrders.isEmpty())
            throw new IllegalStateException(String.format("Order for client %s already exists", aClientId));

        var clientId = ClientId.fromValue(aClientId);
        var client = clientRepository.findClientById(clientId)
                .orElseThrow(() -> new ClientNotFoundException(String.format("Client with id %s is not found", clientId)));

        var newOrder = Order.newOrder(clientId, client.getAddress());
        domainEventPublisher.publish(newOrder.getDomainEvents());

        orderRepository.save(newOrder);
        log.info("Order with id {} has been created", newOrder.getOrderId().toString());
        return orderAssembler.writeDTO(newOrder);
    }

    @Transactional
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('BUYER')")
    public void addProductToOrder(AddProductToOrderCommand command) {
        var order = orderRepository.findOrderById(OrderId.fromValue(command.getOrderId()))
                .orElseThrow(() -> new OrderNotFoundException(
                        String.format("Order with id %s is not found", command.getOrderId())));

        order.addProduct(
                ProductId.fromValue(command.getProductId()),
                Amount.fromValue(command.getPrice()),
                command.getQuantity()
        );

        domainEventPublisher.publish(order.getDomainEvents());
        orderRepository.save(order);

        log.info("Product with id {} has been added to order, id {}",
                command.getProductId(),
                command.getOrderId());
    }

    @Transactional
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('BUYER')")
    public void removeProductFromOrder(String orderId, String productId) {
        var order = orderRepository.findOrderById(OrderId.fromValue(orderId))
                .orElseThrow(() -> new OrderNotFoundException(String.format("Order with id %s is not found", orderId)));

        order.removeProduct(ProductId.fromValue(productId));

        domainEventPublisher.publish(order.getDomainEvents());
        orderRepository.save(order);

        log.info("Product with id {} has been removed from order, id {}", productId, orderId);
    }

    @Transactional
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('BUYER')")
    public void clearOrder(String orderId) {
        var order = orderRepository.findOrderById(OrderId.fromValue(orderId))
                .orElseThrow(() -> new OrderNotFoundException(String.format("Order with id %s is not found", orderId)));

        order.clearProducts();

        domainEventPublisher.publish(order.getDomainEvents());
        orderRepository.save(order);

        log.info("Order with id {} has been cleaned", orderId);
    }

    @Transactional
    public void confirmOrderPayment(String orderId, String paymentId) {
        var order = orderRepository.findOrderById(OrderId.fromValue(orderId))
                .orElseThrow(() -> new OrderNotFoundException(String.format("Order with id %s is not found", orderId)));

        order.confirmPayment(new PaymentId(paymentId));

        domainEventPublisher.publish(order.getDomainEvents());
        orderRepository.save(order);

        log.info("Payment for order with id {} has been confirmed", orderId);
    }

    @Transactional
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('COURIER')")
    public void takeOrderToDelivery(TakeOrderToDeliveryCommand command) {
        var order = orderRepository.findOrderById(OrderId.fromValue(command.getOrderId()))
                .orElseThrow(() -> new OrderNotFoundException(String.format("Order with id %s is not found", command.getOrderId())));

        order.takeInDelivery(EmployeeId.fromValue(command.getCourierId()));

        domainEventPublisher.publish(order.getDomainEvents());
        orderRepository.save(order);

        log.info("Order with id {} has been sent to delivery service", command.getOrderId());
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
    public List<OrderDTO> findPendingProductsOrders(String clientId) {
        return orderRepository.findPendingProductsOrdersForClient(ClientId.fromValue(clientId))
                .stream()
                .map(orderAssembler::writeDTO)
                .collect(Collectors.toList());
    }

}
