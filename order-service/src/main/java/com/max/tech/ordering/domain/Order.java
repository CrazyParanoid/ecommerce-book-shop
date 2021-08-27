package com.max.tech.ordering.domain;

import com.max.tech.ordering.domain.common.AggregateRoot;
import com.max.tech.ordering.domain.payment.PaymentId;
import com.max.tech.ordering.domain.person.PersonId;
import com.max.tech.ordering.domain.product.Product;
import com.max.tech.ordering.domain.product.ProductId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Entity
@Table(name = "orders")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
            targetEntity = Product.class,
            mappedBy = "order",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    private final Set<Product> products = new HashSet<>();
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
     * Add selected product to order. If a product with this ID already exists, it will be updated.
     * The total cost of the order will be recalculated.
     * The discount will also be calculated according to the domain rules.
     *
     * @param productId selected product ID
     * @param price     selected product price
     * @param quantity  selected product quantity
     */
    public void addProduct(ProductId productId, Amount price, Integer quantity) {
        if (this.status != Status.PENDING_PAYMENT)
            throw new IllegalStateException("Wrong invocation for current state");

        this.products.stream()
                .filter(p -> p.getProductId().equals(productId))
                .findAny()
                .ifPresentOrElse(product -> updateExistedProduct(product, price, quantity),
                        () -> addNewProduct(productId, price, quantity));
    }

    private void addNewProduct(ProductId productId, Amount price, Integer quantity) {
        var product = new Product(productId, price, quantity, this);
        product.validate();

        this.products.add(product);
        calculateTotalPrice(price, quantity);

        raiseDomainEvent(new ProductAddedToOrder(
                this.orderId,
                productId,
                product.getQuantity(),
                this.totalPrice));
    }

    private void updateExistedProduct(Product product, Amount newPrice, Integer newQuantity) {
        reduceTotalPrice(product);
        product.update(newPrice, newQuantity);
        calculateTotalPrice(newPrice, newQuantity);

        raiseDomainEvent(new ProductInOrderUpdated(
                this.orderId,
                product.getProductId(),
                newPrice,
                newQuantity,
                this.totalPrice));
    }


    private void calculateTotalPrice(Amount price, Integer quantity) {
        this.totalPrice = this.totalPrice.add(
                price.multiply(Double.valueOf(quantity))
        );
        if (this.totalPrice.greaterOrEquals(DISCOUNT_THRESHOLD)) {
            var discountValue = this.totalPrice.multiply(DISCOUNT_PERCENTAGE / 100);
            this.totalPrice = this.totalPrice.subtract(discountValue);
        }
    }

    public void removeProduct(ProductId productId) {
        if (this.status != Status.PENDING_PAYMENT)
            throw new IllegalStateException("Wrong invocation for current state");
        if (this.products.isEmpty())
            throw new IllegalStateException("Order products can't be empty");

        var product = findProductById(productId);

        reduceTotalPrice(product);
        this.products.remove(product);

        raiseDomainEvent(new ProductRemovedFromOrder(this.orderId, productId, this.totalPrice));
    }

    private void reduceTotalPrice(Product product) {
        var totalPriceWithoutDiscount = this.products.stream()
                .map(Product::totalPrice)
                .reduce(Amount::add)
                .orElseThrow(() -> new IllegalStateException("Total price without discount can't be empty"));

        if (totalPriceWithoutDiscount.greaterOrEquals(DISCOUNT_THRESHOLD))
            this.totalPrice = totalPriceWithoutDiscount;

        this.totalPrice = this.totalPrice.subtract(product.totalPrice());
    }

    public void clearProducts() {
        if (this.status != Status.PENDING_PAYMENT)
            throw new IllegalStateException("Wrong invocation for current state");

        this.products.clear();
        raiseDomainEvent(new OrderCleaned(this.orderId));
    }

    public void confirmPayment(PaymentId paymentId) {
        if (this.status != Status.PENDING_PAYMENT)
            throw new IllegalStateException("Wrong invocation for current state");
        if (this.products.isEmpty())
            throw new IllegalStateException("Order products can't be empty");

        this.paymentId = paymentId;
        this.status = Status.PENDING_DELIVERY_SERVICE;

        raiseDomainEvent(new OrderPaid(this.orderId, paymentId));
    }

    public void takeInDelivery(PersonId courierId) {
        if (this.status != Status.PENDING_DELIVERY_SERVICE)
            throw new IllegalStateException("Wrong invocation for current state");

        this.courierId = courierId;
        this.status = Status.PENDING_FOR_DELIVERING;

        raiseDomainEvent(new OrderTookInDelivery(this.orderId, this.courierId));
    }

    public void deliver() {
        if (this.status != Status.PENDING_FOR_DELIVERING)
            throw new IllegalStateException("Wrong invocation for current state");

        this.deliveredAt = LocalDateTime.now();
        this.status = Status.DELIVERED;
        var productsQuantitiesMap = this.products.stream()
                .collect(Collectors.toMap(
                        Product::getProductId,
                        Product::getQuantity));

        raiseDomainEvent(new OrderDelivered(
                this.orderId,
                this.deliveredAt,
                productsQuantitiesMap));
    }

    public Product findProductById(ProductId productId) {
        return this.products.stream()
                .filter(p -> p.getProductId().equals(productId))
                .findAny()
                .orElseThrow(() -> new IllegalStateException(String.format("Product with id %s is not found",
                        productId.toString())));
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
                order.deliveryAddressId));

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
        PENDING_DELIVERY_SERVICE,
        PENDING_FOR_DELIVERING,
        DELIVERED
    }

}
