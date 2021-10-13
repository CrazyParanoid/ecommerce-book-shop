package com.max.tech.ordering.domain;

import com.max.tech.ordering.domain.common.AggregateRoot;
import com.max.tech.ordering.domain.payment.PaymentId;
import com.max.tech.ordering.domain.person.PersonId;
import com.max.tech.ordering.domain.item.OrderItem;
import com.max.tech.ordering.domain.item.OrderItemId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "orders")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@NamedEntityGraph(name = "order-graph", attributeNodes = {@NamedAttributeNode("items")})
public class Order extends AggregateRoot {
    @Transient
    private static final Amount DISCOUNT_THRESHOLD = Amount.fromValue(BigDecimal.valueOf(15000));
    @Transient
    private static final Double DISCOUNT_PERCENTAGE = 6d;

    @EmbeddedId
    private OrderId orderId;
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "delivery_address_id"))
    private AddressId deliveryAddressId;
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "client_id"))
    private PersonId personId;
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "total_price"))
    private Amount totalPrice = Amount.ZERO_AMOUNT;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "TEXT")
    private Status status = Status.PENDING_PAYMENT;
    @OneToMany(
            targetEntity = OrderItem.class,
            mappedBy = "order",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    private final Set<OrderItem> items = new HashSet<>();
    private LocalDateTime deliveredAt;
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "courier_id"))
    private PersonId courierId;
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "payment_id", columnDefinition = "TEXT"))
    private PaymentId paymentId;

    private Order(PersonId personId, OrderId orderId, AddressId deliveryAddressId) {
        this.personId = personId;
        this.orderId = orderId;
        this.deliveryAddressId = deliveryAddressId;
    }

    /**
     * Add selected item to order.
     * The total cost of the order will be recalculated.
     * The discount will also be calculated according to the domain rules.
     *
     * @param itemId   selected item ID
     * @param price    selected item price
     * @param quantity selected item quantity
     */
    public void addItem(OrderItemId itemId, Amount price, Integer quantity) {
        if (this.status != Status.PENDING_PAYMENT)
            throw new IllegalStateException(String.format("Wrong invocation for current state:" +
                    " expected PENDING_PAYMENT, but actual %s", this.status.name()));

        this.items.stream()
                .filter(item -> item.itemId().equals(itemId))
                .findAny()
                .ifPresentOrElse(item -> updateExistedOrderItem(item, quantity),
                        () -> addNewOrderItem(itemId, price, quantity));
    }

    private void addNewOrderItem(OrderItemId itemId, Amount price, Integer quantity) {
        var item = new OrderItem(itemId, price, quantity, this);
        item.validate();

        this.items.add(item);
        this.totalPrice = calculateTotalPrice();

        raiseDomainEvent(new OrderItemAdded(
                this.orderId,
                itemId,
                item.quantity(),
                this.totalPrice));
    }

    private void updateExistedOrderItem(OrderItem item, Integer quantity) {
        item.update(quantity);
        this.totalPrice = calculateTotalPrice();

        raiseDomainEvent(
                new OrderItemUpdated(
                        this.orderId,
                        item.itemId(),
                        item.quantity(),
                        this.totalPrice
                )
        );
    }

    private Amount calculateTotalPrice() {
        var totalPrice = this.items.stream()
                .map(OrderItem::totalPrice)
                .reduce(Amount::add)
                .orElseThrow(() -> new IllegalStateException("Impossible to reduce total price. " +
                        "Total price without discount can't be empty"));

        //Calculate discount
        if (totalPrice.greaterOrEquals(DISCOUNT_THRESHOLD))
            totalPrice =  totalPrice.percentage(DISCOUNT_PERCENTAGE);

        return totalPrice;
    }

    public void removeItem(OrderItemId itemId) {
        if (this.status != Status.PENDING_PAYMENT)
            throw new IllegalStateException(String.format("Wrong invocation for current state:" +
                    " expected PENDING_PAYMENT, but actual %s", this.status.name()));
        if (this.items.isEmpty())
            throw new IllegalStateException(String.format("Item with id %s can't be deleted." +
                    " Order items can't be empty", itemId.toString()));
        if (this.items.size() == 1)
            throw new IllegalStateException("It is not possible to remove an item from an order with only one item.");

        var item = findItemById(itemId);

        this.items.remove(item);
        this.totalPrice = calculateTotalPrice();

        raiseDomainEvent(new OrderItemRemoved(this.orderId, itemId, this.totalPrice));
    }

    public void confirmPayment(PaymentId paymentId) {
        if (this.status != Status.PENDING_PAYMENT)
            throw new IllegalStateException(String.format("Wrong invocation for current state:" +
                    " expected PENDING_PAYMENT, but actual %s", this.status.name()));
        if (this.items.isEmpty())
            throw new IllegalStateException("Payment can't be confirmed. Order items can't be empty");

        this.paymentId = paymentId;
        this.status = Status.PENDING_COURIER_ASSIGMENT;

        raiseDomainEvent(new OrderPaid(this.orderId, this.paymentId, this.items));
    }

    public void assignCourier(PersonId courierId) {
        if (this.status != Status.PENDING_COURIER_ASSIGMENT)
            throw new IllegalStateException(String.format("Wrong invocation for current state:" +
                    " expected PENDING_DELIVERY_SERVICE, but actual %s", this.status.name()));

        this.courierId = courierId;
        this.status = Status.PENDING_FOR_DELIVERING;

        raiseDomainEvent(new OrderCourierAssigned(this.orderId, this.courierId));
    }

    public void deliver() {
        if (this.status != Status.PENDING_FOR_DELIVERING)
            if (this.status != Status.PENDING_COURIER_ASSIGMENT)
                throw new IllegalStateException(String.format("Wrong invocation for current state:" +
                        " expected PENDING_FOR_DELIVERING, but actual %s", this.status.name()));

        this.deliveredAt = LocalDateTime.now();
        this.status = Status.DELIVERED;

        raiseDomainEvent(new OrderDelivered(
                this.orderId,
                this.deliveredAt));
    }

    public OrderItem findItemById(OrderItemId itemId) {
        return this.items.stream()
                .filter(item -> item.itemId().equals(itemId))
                .findAny()
                .orElseThrow(() -> new IllegalStateException(String.format("Item with id %s is not found",
                        itemId.toString())));
    }

    public static Order place(PersonId personId, AddressId deliveryAddressId) {
        var order = new Order(
                personId,
                OrderId.newOrderId(),
                deliveryAddressId
        );
        order.raiseDomainEvent(new OrderPlaced(
                order.orderId,
                order.personId,
                order.deliveryAddressId,
                order.items));

        return order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Order order = (Order) o;
        return Objects.equals(orderId, order.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }

    public enum Status {
        PENDING_PAYMENT,
        PENDING_COURIER_ASSIGMENT,
        PENDING_FOR_DELIVERING,
        DELIVERED
    }

}
