package com.max.tech.payment.core;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@Table(name = "payments")
@ApiModel(description = "Payment")
@JsonInclude(JsonInclude.Include.NON_NULL)
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true, value = {"hibernateLazyInitializer", "handler"})
public class Payment {
    @Id
    @Column(name = "id", columnDefinition = "TEXT")
    @ApiModelProperty(value = "Payment id", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private String id;
    @Type(type = "org.hibernate.type.UUIDBinaryType")
    @Column(name = "order_id")
    @ApiModelProperty(value = "Order id", accessMode = ApiModelProperty.AccessMode.READ_ONLY, position = 1)
    private UUID orderId;
    @ApiModelProperty(value = "Amount", accessMode = ApiModelProperty.AccessMode.READ_ONLY, position = 2)
    private Long amount;
    @Type(type = "org.hibernate.type.UUIDBinaryType")
    @Column(name = "client_id")
    @ApiModelProperty(value = "Client id", accessMode = ApiModelProperty.AccessMode.READ_ONLY, position = 3)
    private UUID clientId;
    @CreatedDate
    @Column(name = "created_at", columnDefinition = "DATE")
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Column(name = "updated_at", columnDefinition = "DATE")
    private LocalDateTime updatedAt;

    private Payment(String id, UUID orderId, Long amount, UUID clientId) {
        this.id = id;
        this.orderId = orderId;
        this.amount = amount;
        this.clientId = clientId;
    }

    public static Payment newPayment(String id, String orderId, Long amount, String clientId) {
        return new Payment(
                id,
                UUID.fromString(orderId),
                amount,
                UUID.fromString(clientId)
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Payment payment = (Payment) o;
        return Objects.equals(id, payment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
