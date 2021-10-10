package com.max.tech.ordering.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.max.tech.ordering.application.OrderNotFoundException;
import com.max.tech.ordering.application.OrderService;
import com.max.tech.ordering.application.TestApplicationObjectsFactory;
import com.max.tech.ordering.application.dto.AddItemToOrderCommand;
import com.max.tech.ordering.application.dto.PlaceOrderCommand;
import com.max.tech.ordering.config.TestAuthenticationConfig;
import com.max.tech.ordering.helper.TestValues;
import com.max.tech.ordering.helper.WebUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ActiveProfiles({"security", "it"})
@Import(TestAuthenticationConfig.class)
@WebMvcTest(value = OrderController.class)
public class OrderControllerAPITest {
    @MockBean
    private OrderService orderService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        WebUtil.mockSecurity();
    }

    @Test
    @SneakyThrows
    public void shouldPostNewOrder() {
        Mockito.when(orderService.placeOrder(ArgumentMatchers.any(PlaceOrderCommand.class)))
                .thenReturn(TestApplicationObjectsFactory.newOrderDTO());

        var actualResponse = postNewOrder();

        var expectedResponse = WebUtil.getResponse("classpath:json/order_response.json");
        JSONAssert.assertEquals(actualResponse, expectedResponse, JSONCompareMode.LENIENT);
    }

    @SneakyThrows
    private String postNewOrder() {
        return mockMvc.perform(
                        MockMvcRequestBuilders.post(
                                        "/api/v1/orders"
                                )
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + WebUtil.createToken())
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(
                                        TestApplicationObjectsFactory.newPlaceOrderCommand()
                                ))
                ).andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    @SneakyThrows
    public void shouldDeleteItem() {
        Mockito.when(orderService.removeItemFromOrder(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString())).thenReturn(TestApplicationObjectsFactory.newOrderDTOWithOneItem());

        var actualResponse = deleteItem();

        var expectedResponse = WebUtil.getResponse("classpath:json/order_response_with_one_item.json");
        JSONAssert.assertEquals(actualResponse, expectedResponse, JSONCompareMode.LENIENT);
    }

    @SneakyThrows
    private String deleteItem() {
        return mockMvc.perform(
                        MockMvcRequestBuilders.delete(
                                        "/api/v1/orders/{id}/items/{itemId}",
                                        TestValues.ORDER_ID, TestValues.FIRST_ITEM_ID
                                )
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + WebUtil.createToken())
                                .contentType("application/json")
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    public void shouldPostCourier() {
        Mockito.doNothing().when(orderService)
                .assignCourierToOrder(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());

        var actualResponse = postCourier();

        Assertions.assertTrue(actualResponse.isBlank());
    }

    @SneakyThrows
    private String postCourier() {
        return mockMvc.perform(
                        MockMvcRequestBuilders.post(
                                        "/api/v1/orders/{orderId}/courier",
                                        TestValues.ORDER_ID
                                )
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + WebUtil.createToken())
                                .contentType("application/json")
                ).andExpect(MockMvcResultMatchers.status().isNoContent())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    public void shouldPostDelivery() {
        Mockito.doNothing().when(orderService).deliverOrder(ArgumentMatchers.anyString());

        var actualResponse = postDelivery();

        Assertions.assertTrue(actualResponse.isBlank());
    }

    @SneakyThrows
    private String postDelivery() {
        return mockMvc.perform(
                        MockMvcRequestBuilders.post(
                                        "/api/v1/orders/{orderId}/delivery",
                                        TestValues.ORDER_ID
                                )
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + WebUtil.createToken())
                                .contentType("application/json")
                ).andExpect(MockMvcResultMatchers.status().isNoContent())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    @SneakyThrows
    public void shouldReturnOrderById() {
        Mockito.when(orderService.findOrderById(ArgumentMatchers.anyString()))
                .thenReturn(TestApplicationObjectsFactory.newOrderDTO());

        var actualResponse = getOrder(MockMvcResultMatchers.status().isOk());

        var expectedResponse = WebUtil.getResponse("classpath:json/order_response.json");
        JSONAssert.assertEquals(actualResponse, expectedResponse, JSONCompareMode.LENIENT);
    }

    @SneakyThrows
    private String getOrder(ResultMatcher resultMatcher) {
        return mockMvc.perform(
                        MockMvcRequestBuilders.get(
                                        "/api/v1/orders/{orderId}",
                                        TestValues.ORDER_ID
                                )
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + WebUtil.createToken())
                                .contentType("application/json")
                ).andExpect(resultMatcher)
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    @SneakyThrows
    public void shouldReturn404Response() {
        Mockito.when(orderService.findOrderById(ArgumentMatchers.anyString()))
                .thenThrow(new OrderNotFoundException(String.format("Order with id %s is not found", TestValues.ORDER_ID)));

        var actualResponse = getOrder(MockMvcResultMatchers.status().isNotFound());

        var expectedResponse = WebUtil.getResponse("classpath:json/404_not_found_response.json");
        JSONAssert.assertEquals(
                expectedResponse, actualResponse,
                new CustomComparator(JSONCompareMode.LENIENT, new Customization("timestamp", (o, t1) -> true))
        );
    }

    @Test
    @SneakyThrows
    public void shouldPutOrderItem() {
        Mockito.when(orderService.addItemToOrder(ArgumentMatchers.any(AddItemToOrderCommand.class)))
                .thenReturn(TestApplicationObjectsFactory.newOrderDTO());

        var actualResponse = putOrderItem();

        var expectedResponse = WebUtil.getResponse("classpath:json/order_response.json");
        JSONAssert.assertEquals(actualResponse, expectedResponse, JSONCompareMode.LENIENT);
    }

    @SneakyThrows
    private String putOrderItem() {
        return mockMvc.perform(
                        MockMvcRequestBuilders.put(
                                        "/api/v1/orders/{orderId}/items",
                                        TestValues.ORDER_ID
                                )
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + WebUtil.createToken())
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(
                                        TestApplicationObjectsFactory.newAddItemToOrderCommand()
                                ))
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

}
