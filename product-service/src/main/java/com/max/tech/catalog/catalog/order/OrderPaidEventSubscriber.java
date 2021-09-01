package com.max.tech.catalog.catalog.order;

import com.max.tech.catalog.catalog.product.ProductRepository;
import com.max.tech.catalog.catalog.web.User;
import com.max.tech.catalog.catalog.web.UserAuthentication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class OrderPaidEventSubscriber {
    private final ProductRepository productRepository;

    public OrderPaidEventSubscriber(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    @StreamListener(
            target = InputBindings.OrderChannel,
            condition = "headers['type']=='OrderPaid'"
    )
    public void onEvent(OrderPaidEvent event) {
        log.info("OrderPaid event has been received");
        authorizeAsAdmin();

        event.getItems().forEach(item -> {
            var optionalProduct = productRepository.findById(UUID.fromString(item.getItemId()));

            if (optionalProduct.isPresent()) {
                var product = optionalProduct.get();
                product.reduceQuantity(item.getQuantity());

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
