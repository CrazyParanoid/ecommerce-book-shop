package com.max.tech.ordering.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.max.tech.ordering.application.TestApplicationObjectsFactory;
import com.max.tech.ordering.application.order.OrderNotFoundException;
import com.max.tech.ordering.application.order.OrderService;
import com.max.tech.ordering.application.order.dto.AddProductsToOrderCommand;
import com.max.tech.ordering.application.order.dto.CreateNewOrderCommand;
import com.max.tech.ordering.application.order.dto.TakeOrderToDeliveryCommand;
import com.max.tech.ordering.config.TestAuthenticationConfig;
import com.max.tech.ordering.util.TestValues;
import com.max.tech.ordering.util.WebUtil;
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

@ActiveProfiles("security")
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
    public void test_post_new_order() {
        Mockito.when(orderService.createNewOrder(ArgumentMatchers.any(CreateNewOrderCommand.class)))
                .thenReturn(TestApplicationObjectsFactory.newOrderDTO());

        var actualResponse = postNewOrder();

        var expectedResponse = WebUtil.getResponse("classpath:json/new_order_response.json");
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
                                TestApplicationObjectsFactory.newCreateNewOrderCommand()
                        ))
        ).andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    public void test_put_new_product() {
        Mockito.doNothing().when(orderService).addProductsToOrder(ArgumentMatchers.any(AddProductsToOrderCommand.class));

        var actualResponse = putNewProduct();

        Assertions.assertTrue(actualResponse.isBlank());
    }

    @SneakyThrows
    private String putNewProduct() {
        return mockMvc.perform(
                MockMvcRequestBuilders.put(
                        "/api/v1/orders/{id}/products", TestValues.ORDER_ID
                )
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + WebUtil.createToken())
                        .contentType("application/json")
                        .content(
                                objectMapper.writeValueAsString(
                                        TestApplicationObjectsFactory.newAddProductToOrderCommand()
                                )
                        )
        ).andExpect(MockMvcResultMatchers.status().isNoContent())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    public void test_delete_product() {
        Mockito.doNothing().when(orderService).removeProductFromOrder(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString());

        var actualResponse = deleteProduct();

        Assertions.assertTrue(actualResponse.isBlank());
    }

    @SneakyThrows
    private String deleteProduct() {
        return mockMvc.perform(
                MockMvcRequestBuilders.delete(
                        "/api/v1/orders/{id}/products/{productId}",
                        TestValues.ORDER_ID, TestValues.FIRST_PRODUCT_ID
                )
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + WebUtil.createToken())
                        .contentType("application/json")
        ).andExpect(MockMvcResultMatchers.status().isNoContent())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    public void test_delete_products_from_order() {
        Mockito.doNothing().when(orderService).clearOrder(ArgumentMatchers.anyString());

        var actualResponse = deleteProductsFromOrder();

        Assertions.assertTrue(actualResponse.isBlank());
    }

    @SneakyThrows
    private String deleteProductsFromOrder() {
        return mockMvc.perform(
                MockMvcRequestBuilders.delete(
                        "/api/v1/orders/{orderId}",
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
    public void test_post_delivery() {
        Mockito.doNothing().when(orderService)
                .takeOrderToDelivery(ArgumentMatchers.any(TakeOrderToDeliveryCommand.class));

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
                        .content(
                                objectMapper.writeValueAsString(TestApplicationObjectsFactory.newTakeOrderToDeliveryCommand())
                        )
        ).andExpect(MockMvcResultMatchers.status().isNoContent())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    public void test_patch_delivery() {
        Mockito.doNothing().when(orderService).deliverOrder(ArgumentMatchers.anyString());

        var actualResponse = patchDelivery();

        Assertions.assertTrue(actualResponse.isBlank());
    }

    @SneakyThrows
    private String patchDelivery() {
        return mockMvc.perform(
                MockMvcRequestBuilders.patch(
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
    public void test_get_order_by_id() {
        Mockito.when(orderService.findOrderById(ArgumentMatchers.anyString()))
                .thenReturn(TestApplicationObjectsFactory.newOrderDTOWithProducts());

        var actualResponse = getOrder(MockMvcResultMatchers.status().isOk());

        var expectedResponse = WebUtil.getResponse("classpath:json/order_with_products_response.json");
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
    public void test_get_404_response() {
        Mockito.when(orderService.findOrderById(ArgumentMatchers.anyString()))
                .thenThrow(new OrderNotFoundException(String.format("Order with id %s is not found", TestValues.ORDER_ID)));

        var actualResponse = getOrder(MockMvcResultMatchers.status().isNotFound());

        var expectedResponse = WebUtil.getResponse("classpath:json/404_not_found_response.json");
        JSONAssert.assertEquals(
                expectedResponse, actualResponse,
                new CustomComparator(JSONCompareMode.LENIENT, new Customization("timestamp", (o, t1) -> true))
        );
    }

}
