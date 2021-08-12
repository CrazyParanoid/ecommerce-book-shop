package com.max.tech.ordering.domain;

import com.max.tech.ordering.domain.client.Client;
import com.max.tech.ordering.util.AssertionUtil;
import com.max.tech.ordering.util.TestValues;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ClientTest {

    @Test
    public void test_create_new_client() {
        var client = Client.newBuilder()
                .withId(TestValues.CLIENT_ID)
                .withAddress(
                        TestValues.CITY,
                        TestValues.STREET,
                        TestValues.HOUSE,
                        TestValues.FLAT,
                        TestValues.FLOOR,
                        TestValues.ENTRANCE
                )
                .build();

        AssertionUtil.assertAddress(client.getAddress());
        Assertions.assertEquals(client.getClientId().toString(), TestValues.CLIENT_ID);
    }

}
