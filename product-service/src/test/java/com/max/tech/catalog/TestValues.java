package com.max.tech.catalog;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@UtilityClass
public class TestValues {
    public final UUID FIRST_PRODUCT_ID = UUID.fromString("331b05c3-23e5-4297-a463-d0ebfc9a4920");
    public final Integer FIRST_PRODUCT_QUANTITY = 4;
    public final String CLIENT_ID = "d4e30469-60c2-4ea2-a01b-35ea9b13d07c";
    public final String PICTURE_LINK = "testLink";
    public final BigDecimal PRICE = new BigDecimal(6000).setScale(6, RoundingMode.UP);
    public final String NAME = "The Art of Computer Programming";
    public final String AUTHOR = "Donald Knuth";
    public final Integer QUANTITY = 7;
}
