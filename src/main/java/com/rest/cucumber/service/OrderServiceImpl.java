package com.rest.cucumber.service;

import org.springframework.stereotype.Service;

import com.rest.cucumber.model.Order;

@Service
public class OrderServiceImpl implements OrderService {
    @Override
    public Order getOrderObject() {
        return new Order(123, 44, 33.55, "CUST-ID-123");
    }
}
