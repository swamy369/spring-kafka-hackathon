package com.hackathon.orderservice.event;

public record FraudResultEvent(String orderId, boolean approved, String reason) {
}
