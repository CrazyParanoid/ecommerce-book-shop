package com.max.tech.ordering.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.max.tech.ordering.application.TestApplicationObjectsFactory;
import com.max.tech.ordering.application.client.ClientService;
import com.max.tech.ordering.application.client.dto.RegisterNewClientCommand;
import com.max.tech.ordering.config.TestAuthenticationConfig;
import com.max.tech.ordering.util.TestValues;
import com.max.tech.ordering.util.WebUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ActiveProfiles("security")
@Import(TestAuthenticationConfig.class)
@WebMvcTest(value = ClientController.class)
public class ClientControllerAPITest {
    @MockBean
    private ClientService clientService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @SneakyThrows
    public void test_register_new_client() {
        Mockito.when(clientService.registerNewClient(ArgumentMatchers.any(RegisterNewClientCommand.class)))
                .thenReturn(TestApplicationObjectsFactory.newClientDTO());

        var actualResponse = postClient();

        var expectedResponse = WebUtil.getResponse("classpath:json/new_registered_client_response.json");
        JSONAssert.assertEquals(actualResponse, expectedResponse, JSONCompareMode.LENIENT);
    }

    @SneakyThrows
    private String postClient() {
        return mockMvc.perform(
                MockMvcRequestBuilders.post(
                        "/api/clients"
                )
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + WebUtil.createToken())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(
                                TestApplicationObjectsFactory.newRegisterNewClientCommand())
                        )
        ).andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    @SneakyThrows
    public void test_find_client_by_id() {
        Mockito.when(clientService.findClientById(ArgumentMatchers.anyString()))
                .thenReturn(TestApplicationObjectsFactory.newClientDTO());

        var actualResponse = getClient();

        var expectedResponse = WebUtil.getResponse("classpath:json/new_registered_client_response.json");
        JSONAssert.assertEquals(actualResponse, expectedResponse, JSONCompareMode.LENIENT);
    }

    @SneakyThrows
    private String getClient() {
        return mockMvc.perform(
                MockMvcRequestBuilders.get(
                        "/api/clients/{id}", TestValues.CLIENT_ID
                )
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + WebUtil.createToken())
                        .contentType("application/json")
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    public void test_delete_client() {
        Mockito.doNothing().when(clientService).removeClient(ArgumentMatchers.anyString());

        var actualResponse = deleteClient();

        Assertions.assertTrue(actualResponse.isBlank());
    }

    @SneakyThrows
    private String deleteClient() {
        return mockMvc.perform(
                MockMvcRequestBuilders.delete(
                        "/api/clients/{id}", TestValues.CLIENT_ID
                )
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + WebUtil.createToken())
                        .contentType("application/json")
        ).andExpect(MockMvcResultMatchers.status().isNoContent())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

}
