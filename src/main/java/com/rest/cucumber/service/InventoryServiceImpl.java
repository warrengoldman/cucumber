package com.rest.cucumber.service;

import org.springframework.stereotype.Service;

@Service
public class InventoryServiceImpl implements InventoryService {
    @Override
    public int getQty(int key) {
        return 1;
    }

    @Override
    public void reduceInventory(int qty) {
        // adding to inventory
    }
}
