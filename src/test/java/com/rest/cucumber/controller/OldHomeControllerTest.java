package com.rest.cucumber.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rest.cucumber.model.Order;
import com.rest.cucumber.service.InventoryServiceImpl;
import com.rest.cucumber.service.OrderServiceImpl;
import com.rest.cucumber.service.SalesServiceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class OldHomeControllerTest {
    private MockMvc mockMvc;

    @Mock
    private OrderServiceImpl orderService;

    @Mock
    private SalesServiceImpl salesService;

    @Mock
    private InventoryServiceImpl inventoryService;

    @InjectMocks
    private HomeController homeController;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this); // Initializes mocks for older Mockito versions
        mockMvc = MockMvcBuilders.standaloneSetup(homeController).build();
    }

    @Test
    public void processOrder() throws Exception {
        when(orderService.getOrderObject()).thenCallRealMethod();
        Order expectedOrder = orderService.getOrderObject();
        when(inventoryService.getQty(orderService.getOrderObject().key())).thenReturn(4);
        MvcResult result = mockMvc.perform(get("/home/process-order").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        Order actualObject = objectMapper.readValue(responseBody, Order.class);
        assertEquals(expectedOrder, actualObject);
        verify(inventoryService, times(1)).getQty(expectedOrder.key());
        verify(salesService, times(1)).addRevenue(expectedOrder.price()*expectedOrder.qty());
    }
}
