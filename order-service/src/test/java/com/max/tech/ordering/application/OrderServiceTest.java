package com.max.tech.ordering.application;

import com.max.tech.ordering.application.dto.OrderDTO;
import com.max.tech.ordering.domain.Order;
import com.max.tech.ordering.domain.OrderId;
import com.max.tech.ordering.domain.OrderRepository;
import com.max.tech.ordering.domain.TestDomainObjectsFactory;
import com.max.tech.ordering.domain.person.PersonId;
import com.max.tech.ordering.domain.common.DomainEvent;
import com.max.tech.ordering.domain.common.DomainEventPublisher;
import com.max.tech.ordering.util.AssertionUtil;
import com.max.tech.ordering.util.TestValues;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class OrderServiceTest {
    private OrderService orderService;
    @Mock
    private DomainEventPublisher domainEventPublisher;
    @Mock
    private OrderRepository orderRepository;

    @Captor
    private ArgumentCaptor<Order> orderCaptor;
    @Captor
    private ArgumentCaptor<List<? super DomainEvent>> domainEventsCaptor;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        this.orderService = new OrderService(orderRepository, domainEventPublisher, new OrderAssembler());
    }

    @Test
    public void test_place_order() {
        Mockito.doNothing().when(orderRepository).save(ArgumentMatchers.any(Order.class));
        Mockito.doNothing().when(domainEventPublisher).publish(ArgumentMatchers.anyList());
        Mockito.when(orderRepository.findPendingPaymentOrdersForClient(ArgumentMatchers.any(PersonId.class)))
                .thenReturn(Collections.emptyList());

        var orderDTO = orderService.placeOrder(
                TestApplicationObjectsFactory.newPlaceOrderCommand()
        );

        AssertionUtil.assertOrderDTO(orderDTO);
    }

    @Test
    public void test_add_product_to_order() {
        Mockito.when(orderRepository.findOrderById(ArgumentMatchers.any(OrderId.class)))
                .thenReturn(Optional.of(TestDomainObjectsFactory.newOrder()));
        Mockito.doNothing().when(orderRepository).save(orderCaptor.capture());
        Mockito.doNothing().when(domainEventPublisher).publish(domainEventsCaptor.capture());

        orderService.addProductsToOrder(TestApplicationObjectsFactory.newAddProductToOrderCommand());

        Mockito.verify(orderRepository, Mockito.times(1)).save(ArgumentMatchers.any(Order.class));
        Mockito.verify(domainEventPublisher, Mockito.times(1)).publish(ArgumentMatchers.anyList());
        Assertions.assertNotNull(orderCaptor.getValue());
        Assertions.assertFalse(domainEventsCaptor.getValue().isEmpty());
    }

    @Test
    public void test_remove_product_from_order() {
        Mockito.when(orderRepository.findOrderById(ArgumentMatchers.any(OrderId.class)))
                .thenReturn(Optional.of(TestDomainObjectsFactory.newOrderWithOneProduct()));
        Mockito.doNothing().when(orderRepository).save(orderCaptor.capture());
        Mockito.doNothing().when(domainEventPublisher).publish(domainEventsCaptor.capture());

        orderService.removeProductFromOrder(TestValues.ORDER_ID, TestValues.FIRST_PRODUCT_ID);

        Mockito.verify(orderRepository, Mockito.times(1)).save(ArgumentMatchers.any(Order.class));
        Mockito.verify(domainEventPublisher, Mockito.times(1)).publish(ArgumentMatchers.anyList());
        Assertions.assertNotNull(orderCaptor.getValue());
        Assertions.assertFalse(domainEventsCaptor.getValue().isEmpty());
    }

    @Test
    public void test_clear_order() {
        Mockito.when(orderRepository.findOrderById(ArgumentMatchers.any(OrderId.class)))
                .thenReturn(Optional.of(TestDomainObjectsFactory.newOrderWithOneProduct()));
        Mockito.doNothing().when(orderRepository).save(orderCaptor.capture());
        Mockito.doNothing().when(domainEventPublisher).publish(domainEventsCaptor.capture());

        orderService.clearOrder(TestValues.ORDER_ID);

        Mockito.verify(orderRepository, Mockito.times(1)).save(ArgumentMatchers.any(Order.class));
        Mockito.verify(domainEventPublisher, Mockito.times(1)).publish(ArgumentMatchers.anyList());
        Assertions.assertNotNull(orderCaptor.getValue());
        Assertions.assertFalse(domainEventsCaptor.getValue().isEmpty());
    }

    @Test
    public void test_confirm_order_payment() {
        Mockito.when(orderRepository.findOrderById(ArgumentMatchers.any(OrderId.class)))
                .thenReturn(Optional.of(TestDomainObjectsFactory.newOrderWithOneProduct()));
        Mockito.doNothing().when(orderRepository).save(orderCaptor.capture());
        Mockito.doNothing().when(domainEventPublisher).publish(domainEventsCaptor.capture());

        orderService.confirmOrderPayment(TestValues.ORDER_ID, TestValues.PAYMENT_ID);

        Mockito.verify(orderRepository, Mockito.times(1)).save(ArgumentMatchers.any(Order.class));
        Mockito.verify(domainEventPublisher, Mockito.times(1)).publish(ArgumentMatchers.anyList());
        Assertions.assertNotNull(orderCaptor.getValue());
        Assertions.assertFalse(domainEventsCaptor.getValue().isEmpty());
    }

    @Test
    public void test_take_order_in_delivery() {
        Mockito.when(orderRepository.findOrderById(ArgumentMatchers.any(OrderId.class)))
                .thenReturn(Optional.of(TestDomainObjectsFactory.newPendingDeliveryServiceOrder()));
        Mockito.doNothing().when(orderRepository).save(orderCaptor.capture());
        Mockito.doNothing().when(domainEventPublisher).publish(domainEventsCaptor.capture());

        orderService.takeOrderToDelivery(TestApplicationObjectsFactory.newTakeOrderToDeliveryCommand());

        Mockito.verify(orderRepository, Mockito.times(1)).save(ArgumentMatchers.any(Order.class));
        Mockito.verify(domainEventPublisher, Mockito.times(1)).publish(ArgumentMatchers.anyList());
        Assertions.assertNotNull(orderCaptor.getValue());
        Assertions.assertFalse(domainEventsCaptor.getValue().isEmpty());
    }

    @Test
    public void test_deliver_order() {
        Mockito.when(orderRepository.findOrderById(ArgumentMatchers.any(OrderId.class)))
                .thenReturn(Optional.of(TestDomainObjectsFactory.newPendingForDeliveringOrder()));
        Mockito.doNothing().when(orderRepository).save(orderCaptor.capture());
        Mockito.doNothing().when(domainEventPublisher).publish(domainEventsCaptor.capture());

        orderService.deliverOrder(TestValues.ORDER_ID);

        Mockito.verify(orderRepository, Mockito.times(1)).save(ArgumentMatchers.any(Order.class));
        Mockito.verify(domainEventPublisher, Mockito.times(1)).publish(ArgumentMatchers.anyList());
        Assertions.assertNotNull(orderCaptor.getValue());
        Assertions.assertFalse(domainEventsCaptor.getValue().isEmpty());
    }

    @Test
    public void test_find_delivered_order_by_id() {
        Mockito.when(orderRepository.findOrderById(ArgumentMatchers.any(OrderId.class)))
                .thenReturn(Optional.of(TestDomainObjectsFactory.newDeliveredOrder()));

        var orderDTO = orderService.findOrderById(TestValues.ORDER_ID);

        assertOrderDTO(orderDTO);
    }

    @Test
    public void test_find_orders_for_client() {
        Mockito.when(orderRepository.findPendingPaymentOrdersForClient(ArgumentMatchers.any(PersonId.class)))
                .thenReturn(Collections.singletonList(TestDomainObjectsFactory.newDeliveredOrder()));

        var orderDTOs = orderService.findPendingProductsOrders(TestValues.CLIENT_ID);

        Assertions.assertFalse(orderDTOs.isEmpty());
        orderDTOs.forEach(this::assertOrderDTO);
    }

    private void assertOrderDTO(OrderDTO orderDTO) {
        Assertions.assertNotNull(orderDTO.getOrderId());
        Assertions.assertEquals(orderDTO.getClientId(), TestValues.CLIENT_ID);
        AssertionUtil.assertCurrentDateTime(orderDTO.getDeliveredAt());
        Assertions.assertEquals(orderDTO.getStatus(), Order.Status.DELIVERED.name());
        Assertions.assertEquals(orderDTO.getTotalPrice(), TestValues.TOTAL_ORDER_PRICE_WITH_ONE_PRODUCT);
        Assertions.assertEquals(orderDTO.getCourierId(), TestValues.EMPLOYEE_ID);
        Assertions.assertFalse(orderDTO.getProducts().isEmpty());
        Assertions.assertEquals(orderDTO.getPaymentId(), TestValues.PAYMENT_ID);
        Assertions.assertEquals(orderDTO.getDeliveryAddressId(), TestValues.ADDRESS_ID);

        var productDTO = orderDTO.getProducts()
                .stream()
                .findAny()
                .orElse(null);
        Assertions.assertNotNull(productDTO);
        Assertions.assertEquals(productDTO.getProductId(), TestValues.FIRST_PRODUCT_ID);
        Assertions.assertEquals(productDTO.getPrice(), TestValues.FIRST_PRODUCT_PRICE);
        Assertions.assertEquals(productDTO.getQuantity(), TestValues.FIRST_PRODUCT_QUANTITY);
    }

}
