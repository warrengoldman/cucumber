package com.rest.cucumber.service;

public interface InventoryService {
    public int getQty(int key);
    public void reduceInventory(int qty);
}
