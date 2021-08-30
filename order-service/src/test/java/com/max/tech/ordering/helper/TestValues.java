package com.max.tech.ordering.helper;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.RoundingMode;

@UtilityClass
public class TestValues {
    public final String ORDER_ID = "7fff2f52-4efc-4a40-b2ec-64145e9794a3";
    public final String CLIENT_ID = "d4e30469-60c2-4ea2-a01b-35ea9b13d07c";
    public final String ADDRESS_ID = "c78eeebd-427d-4b30-9431-42ae95bd6766";
    public final String FIRST_ITEM_ID = "7417d778-fabe-4a90-ad93-3dfb74c51608";
    public final BigDecimal FIRST_ITEM_PRICE = new BigDecimal(2100).setScale(6, RoundingMode.UP);
    public final int FIRST_ITEM_QUANTITY = 3;
    public final String PAYMENT_ID = "1";
    public final BigDecimal TOTAL_ORDER_PRICE_WITH_ONE_ITEM = new BigDecimal(6300).setScale(6, RoundingMode.UP);
    public final BigDecimal TOTAL_ORDER_PRICE_WITH_TWO_ITEMS = new BigDecimal(57246).setScale(6, RoundingMode.UP);
    public final BigDecimal ITEM_PRICE_WITH_DISCOUNT = new BigDecimal(17000).setScale(6, RoundingMode.UP);
    public final String SECOND_ITEM_ID = "af57d951-09b5-49ca-a0cb-c11d6c04ac47";
    public final BigDecimal SECOND_ITEM_PRICE = new BigDecimal(7800).setScale(6, RoundingMode.UP);
    public final int SECOND_ITEM_QUANTITY = 7;
    public final BigDecimal ORDER_TOTAL_PRICE = new BigDecimal(9900).setScale(6, RoundingMode.UP);
    public final String EMPLOYEE_ID = "4ca93520-5644-4288-a429-60d039cb1682";
}
