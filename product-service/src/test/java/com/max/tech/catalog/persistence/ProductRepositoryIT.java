package com.max.tech.catalog.persistence;

import com.max.tech.catalog.TestProductFactory;
import com.max.tech.catalog.catalog.Application;
import com.max.tech.catalog.catalog.product.ProductRepository;
import com.max.tech.catalog.config.MongoConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("it")
@Import(MongoConfig.class)
@SpringBootTest(classes = Application.class)
public class ProductRepositoryIT {
    @Autowired
    private ProductRepository productRepository;

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void test_save_product(){
        var product = TestProductFactory.newProduct();
        productRepository.save(product);

        var savedProduct = productRepository.findById(product.getId());
        Assertions.assertNotNull(savedProduct);
    }
}
