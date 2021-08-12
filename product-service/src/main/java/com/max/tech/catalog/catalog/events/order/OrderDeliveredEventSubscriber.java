package com.max.tech.catalog.catalog.events.order;

import com.max.tech.catalog.catalog.events.EventSubscriber;
import com.max.tech.catalog.catalog.events.InputBindings;
import com.max.tech.catalog.catalog.product.ProductRepository;
import com.max.tech.catalog.catalog.web.User;
import com.max.tech.catalog.catalog.web.UserAuthentication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
public class OrderDeliveredEventSubscriber implements EventSubscriber<OrderDelivered> {
    private final ProductRepository productRepository;

    public OrderDeliveredEventSubscriber(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    @StreamListener(
            target = InputBindings.OrderChannel,
            condition = "headers['type']=='OrderDelivered'"
    )
    public void onEvent(OrderDelivered event) {
        log.info("OrderDelivered event has been received");
        authorizeAsAdmin();

        event.getProductsQuantities().forEach((id, quantity) -> {
            var optionalProduct = productRepository.findById(id);

            if (optionalProduct.isPresent()) {
                var product = optionalProduct.get();
                product.reduceQuantity(quantity);

                productRepository.save(product);
                log.info("Product with id {} has been updated", product.getId());
            }
        });
    }

    private void authorizeAsAdmin() {
        SecurityContextHolder.getContext().setAuthentication(
                new UserAuthentication(
                        new User(
                                User.SERVICE_USER_ID,
                                List.of(new User.Role(User.Role.ADMIN_ROLE))
                        )
                )
        );
    }

}
