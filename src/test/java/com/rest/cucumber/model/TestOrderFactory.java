package com.rest.cucumber.model;

import net.datafaker.Faker;
import net.datafaker.service.RandomService;

import java.util.List;

public record TestOrderFactory() {
    public static List<Order> getOrders() {
        return List.of(
                new Order(1,1, 12.55, "Jim Smits-123"),
                new Order(2, 22, 10.55, "Davey Jones-456"),
                new Order(3,12, 11.55, "Marco Polo-789"),
                new Order(4,44, 8.55, "Davey Jones-456")
        );
    }

    public static Order createOrder(String firstName, String lastName, int quantity, double price) {
        return getOrder(firstName, lastName, quantity, price);
    }

    public static Order createOrder() {
        Faker faker = new Faker();
        RandomService random = faker.random();
        int qty = random.nextInt(1, 11);
        double price = random.nextDouble(1.00, 55.00);
        return getOrder(faker.name().firstName(), faker.name().lastName(), qty, price);
    }

    private static Order getOrder(String firstName, String lastName, int quantity, double price) {
        String custId = "%s %s-%d".formatted(firstName, lastName, quantity);
        return new Order(-1, quantity, price, custId);
    }
}