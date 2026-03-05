package com.hackathon.inventoryservice.event;

public record OrderCreatedEvent(String orderId, String customerId, double amount) {
}
