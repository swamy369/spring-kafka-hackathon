package com.hackathon.orderservice.event;

public record OrderCreatedEvent(String orderId, String customerId, double amount) {
}
