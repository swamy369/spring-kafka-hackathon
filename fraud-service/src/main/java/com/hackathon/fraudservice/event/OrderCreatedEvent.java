package com.hackathon.fraudservice.event;

public record OrderCreatedEvent(String orderId, String customerId, double amount) {
}
