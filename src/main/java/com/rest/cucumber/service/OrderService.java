package com.rest.cucumber.service;

import org.springframework.stereotype.Service;

import com.rest.cucumber.model.Order;

@Service
public interface OrderService {
    Order getOrderObject();
}
