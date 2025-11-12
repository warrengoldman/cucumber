package com.rest.cucumber.model;

import java.util.List;

public record Product(String product, List<Order> orders) {}