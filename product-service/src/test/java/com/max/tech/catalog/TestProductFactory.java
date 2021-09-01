package com.max.tech.catalog;

import com.max.tech.catalog.catalog.product.Product;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TestProductFactory {

    public Product newProduct() {
        return new Product(
                TestValues.PRODUCT_ID,
                TestValues.PICTURE_LINK,
                TestValues.PRICE,
                TestValues.NAME,
                TestValues.AUTHOR,
                TestValues.QUANTITY
        );
    }

}
