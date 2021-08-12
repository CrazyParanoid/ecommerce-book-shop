package com.max.tech.catalog.catalog.product;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@Document(collection = "products")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Product {
    @Id
    private UUID id = UUID.randomUUID();
    @NotEmpty(message = "picture_link can't be null or empty")
    private String pictureLink;
    @NotNull(message = "price can't be null")
    private BigDecimal price;
    @NotEmpty(message = "name can't be null or empty")
    private String name;
    @NotEmpty(message = "author can't be null or empty")
    private String author;
    @NotNull(message = "quantity can't be null")
    private Integer quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Product(UUID id, String pictureLink, BigDecimal price,
                   String name, String author, Integer quantity) {
        this.id = id;
        this.pictureLink = pictureLink;
        this.price = price;
        this.name = name;
        this.author = author;
        this.quantity = quantity;
    }

    public void reduceQuantity(Integer quantity){
        this.quantity = this.quantity - quantity;
    }

}
