package com.max.tech.ordering.application;

import com.max.tech.ordering.domain.Order;
import com.max.tech.ordering.domain.OrderId;
import com.max.tech.ordering.domain.OrderRepository;
import com.max.tech.ordering.domain.TestDomainObjectsFactory;
import com.max.tech.ordering.domain.common.DomainEvent;
import com.max.tech.ordering.domain.common.DomainEventPublisher;
import com.max.tech.ordering.domain.person.PersonId;
import com.max.tech.ordering.helper.TestValues;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
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
    public void shouldPlaceOrder() {
        Mockito.doNothing().when(orderRepository).save(ArgumentMatchers.any(Order.class));
        Mockito.doNothing().when(domainEventPublisher).publish(ArgumentMatchers.anyList());
        Mockito.when(orderRepository.findPendingPaymentOrdersForClient(ArgumentMatchers.any(PersonId.class)))
                .thenReturn(Collections.emptyList());

        var orderDTO = orderService.placeOrder(
                TestApplicationObjectsFactory.newPlaceOrderCommand()
        );

        Assertions.assertNotNull(orderDTO.getOrderId());
        Assertions.assertEquals(orderDTO.getClientId(), TestValues.CLIENT_ID);
        Assertions.assertNull(orderDTO.getDeliveredAt());
        Assertions.assertEquals(orderDTO.getStatus(), Order.Status.PENDING_PAYMENT.name());
        Assertions.assertNull(orderDTO.getCourierId());
        Assertions.assertEquals(orderDTO.getTotalPrice(), TestValues.TOTAL_ORDER_PRICE_WITH_ONE_ITEM);
        Assertions.assertFalse(orderDTO.getItems().isEmpty());
        Assertions.assertNull(orderDTO.getPaymentId());
        Assertions.assertEquals(orderDTO.getDeliveryAddressId(), TestValues.ADDRESS_ID);

        var itemDTO = orderDTO.getItems()
                .stream()
                .findFirst()
                .orElse(null);

        Assertions.assertNotNull(itemDTO);
        Assertions.assertEquals(itemDTO.getItemId(), TestValues.FIRST_ITEM_ID);
        Assertions.assertEquals(itemDTO.getPrice(), TestValues.FIRST_ITEM_PRICE);
        Assertions.assertEquals(itemDTO.getQuantity(), TestValues.FIRST_ITEM_QUANTITY);
    }

    @Test
    public void shouldAddItemToOrder() {
        Mockito.when(orderRepository.findOrderById(ArgumentMatchers.any(OrderId.class)))
                .thenReturn(Optional.of(TestDomainObjectsFactory.newOrderWithOneItem()));

        var orderDTO = orderService.addItemToOrder(TestApplicationObjectsFactory.newAddItemToOrderCommand());

        Assertions.assertEquals(orderDTO.getTotalPrice(), TestValues.TOTAL_ORDER_PRICE_WITH_TWO_ITEMS);
        Assertions.assertEquals(2, orderDTO.getItems().size());
        var secondItem = orderDTO.getItems().stream()
                .filter(i -> i.getItemId().equals(TestValues.SECOND_ITEM_ID))
                .findAny()
                .orElse(null);
        Assertions.assertNotNull(secondItem);
        Assertions.assertEquals(secondItem.getItemId(), TestValues.SECOND_ITEM_ID);
        Assertions.assertEquals(secondItem.getPrice(), TestValues.SECOND_ITEM_PRICE);
        Assertions.assertEquals(secondItem.getQuantity(), TestValues.SECOND_ITEM_QUANTITY);
    }

    @Test
    public void shouldRemoveItemFromOrder() {
        Mockito.when(orderRepository.findOrderById(ArgumentMatchers.any(OrderId.class)))
                .thenReturn(Optional.of(TestDomainObjectsFactory.newOrderWithTwoItems()));

        var orderDTO = orderService.removeItemFromOrder(TestValues.ORDER_ID, TestValues.SECOND_ITEM_ID);

        Assertions.assertEquals(orderDTO.getTotalPrice(), TestValues.TOTAL_ORDER_PRICE_WITH_ONE_ITEM);
        Assertions.assertEquals(1, orderDTO.getItems().size());
    }

    @Test
    public void shouldConfirmPayment() {
        Mockito.when(orderRepository.findOrderById(ArgumentMatchers.any(OrderId.class)))
                .thenReturn(Optional.of(TestDomainObjectsFactory.newOrderWithOneItem()));
        Mockito.doNothing().when(orderRepository).save(orderCaptor.capture());
        Mockito.doNothing().when(domainEventPublisher).publish(domainEventsCaptor.capture());

        orderService.confirmOrderPayment(TestValues.ORDER_ID, TestValues.PAYMENT_ID);

        Mockito.verify(orderRepository, Mockito.times(1)).save(ArgumentMatchers.any(Order.class));
        Mockito.verify(domainEventPublisher, Mockito.times(1)).publish(ArgumentMatchers.anyList());
        Assertions.assertNotNull(orderCaptor.getValue());
        Assertions.assertFalse(domainEventsCaptor.getValue().isEmpty());
    }

    @Test
    public void shouldAssignCourierToOrder() {
        Mockito.when(orderRepository.findOrderById(ArgumentMatchers.any(OrderId.class)))
                .thenReturn(Optional.of(TestDomainObjectsFactory.newPendingDeliveryServiceOrder()));
        Mockito.doNothing().when(orderRepository).save(orderCaptor.capture());
        Mockito.doNothing().when(domainEventPublisher).publish(domainEventsCaptor.capture());

        orderService.assignCourierToOrder(TestValues.ORDER_ID, TestValues.EMPLOYEE_ID);

        Mockito.verify(orderRepository, Mockito.times(1)).save(ArgumentMatchers.any(Order.class));
        Mockito.verify(domainEventPublisher, Mockito.times(1)).publish(ArgumentMatchers.anyList());
        Assertions.assertNotNull(orderCaptor.getValue());
        Assertions.assertFalse(domainEventsCaptor.getValue().isEmpty());
    }

    @Test
    public void shouldDeliverOrder() {
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
    public void shouldReturnDeliveredOrderById() {
        Mockito.when(orderRepository.findOrderById(ArgumentMatchers.any(OrderId.class)))
                .thenReturn(Optional.of(TestDomainObjectsFactory.newDeliveredOrder()));

        var orderDTO = orderService.findOrderById(TestValues.ORDER_ID);

        Assertions.assertNotNull(orderDTO.getOrderId());
        Assertions.assertEquals(orderDTO.getClientId(), TestValues.CLIENT_ID);
        Assertions.assertTrue(orderDTO.getDeliveredAt().toLocalDate().isEqual(LocalDate.now()));
        Assertions.assertEquals(orderDTO.getStatus(), Order.Status.DELIVERED.name());
        Assertions.assertEquals(orderDTO.getTotalPrice(), TestValues.TOTAL_ORDER_PRICE_WITH_ONE_ITEM);
        Assertions.assertEquals(orderDTO.getCourierId(), TestValues.EMPLOYEE_ID);
        Assertions.assertFalse(orderDTO.getItems().isEmpty());
        Assertions.assertEquals(orderDTO.getPaymentId(), TestValues.PAYMENT_ID);
        Assertions.assertEquals(orderDTO.getDeliveryAddressId(), TestValues.ADDRESS_ID);

        var itemDTO = orderDTO.getItems()
                .stream()
                .findAny()
                .orElse(null);
        Assertions.assertNotNull(itemDTO);
        Assertions.assertEquals(itemDTO.getItemId(), TestValues.FIRST_ITEM_ID);
        Assertions.assertEquals(itemDTO.getPrice(), TestValues.FIRST_ITEM_PRICE);
        Assertions.assertEquals(itemDTO.getQuantity(), TestValues.FIRST_ITEM_QUANTITY);
    }

    @Test
    public void shouldReturnOrdersForClient() {
        Mockito.when(orderRepository.findPendingPaymentOrdersForClient(ArgumentMatchers.any(PersonId.class)))
                .thenReturn(Collections.singletonList(TestDomainObjectsFactory.newDeliveredOrder()));

        var orderDTOs = orderService.findPendingPaymentOrders(TestValues.CLIENT_ID);

        Assertions.assertFalse(orderDTOs.isEmpty());
        orderDTOs.forEach(orderDTO -> {
            Assertions.assertNotNull(orderDTO.getOrderId());
            Assertions.assertEquals(orderDTO.getClientId(), TestValues.CLIENT_ID);
            Assertions.assertTrue(orderDTO.getDeliveredAt().toLocalDate().isEqual(LocalDate.now()));
            Assertions.assertEquals(orderDTO.getStatus(), Order.Status.DELIVERED.name());
            Assertions.assertEquals(orderDTO.getTotalPrice(), TestValues.TOTAL_ORDER_PRICE_WITH_ONE_ITEM);
            Assertions.assertEquals(orderDTO.getCourierId(), TestValues.EMPLOYEE_ID);
            Assertions.assertFalse(orderDTO.getItems().isEmpty());
            Assertions.assertEquals(orderDTO.getPaymentId(), TestValues.PAYMENT_ID);
            Assertions.assertEquals(orderDTO.getDeliveryAddressId(), TestValues.ADDRESS_ID);

            var itemDTO = orderDTO.getItems()
                    .stream()
                    .findAny()
                    .orElse(null);
            Assertions.assertNotNull(itemDTO);
            Assertions.assertEquals(itemDTO.getItemId(), TestValues.FIRST_ITEM_ID);
            Assertions.assertEquals(itemDTO.getPrice(), TestValues.FIRST_ITEM_PRICE);
            Assertions.assertEquals(itemDTO.getQuantity(), TestValues.FIRST_ITEM_QUANTITY);
        });
    }

}
