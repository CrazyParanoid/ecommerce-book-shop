package com.max.tech.catalog.catalog.events.order;

import com.max.tech.catalog.catalog.events.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDelivered implements Event {
    private Map<UUID, Integer> productsQuantities;
}
