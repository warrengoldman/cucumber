package com.rest.cucumber.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rest.cucumber.model.Order;
import com.rest.cucumber.service.InventoryService;
import com.rest.cucumber.service.OrderService;
import com.rest.cucumber.service.OrderServiceImpl;
import com.rest.cucumber.service.SalesService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(HomeController.class)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;
    @MockitoBean
    private InventoryService inventoryService;
    @MockitoBean
    private SalesService salesService;

    @Test
    public void processOrder_enoughInventory() throws Exception{
        OrderService os = new OrderServiceImpl();
        Order expectedOrder = os.getOrderObject();
        when(orderService.getOrderObject()).thenReturn(expectedOrder);
        when(inventoryService.getQty(anyInt())).thenReturn(1);
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

    @Test
    public void processOrder_notEnoughInventory() throws Exception{
        OrderService os = new OrderServiceImpl();
        when(orderService.getOrderObject()).thenReturn(os.getOrderObject());
        when(inventoryService.getQty(anyInt())).thenReturn(0);
        mockMvc.perform(get("/home/process-order").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void processOrder_allMocks() throws Exception{
        mockMvc.perform(get("/home/process-order"))
                .andExpect(status().isNotFound());
    }
}
