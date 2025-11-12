package com.rest.cucumber.model;

public record Order(int key, int qty, double price, String custId) {}
