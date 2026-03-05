package com.hackathon.fraudservice.event;

public record FraudResultEvent(String orderId, boolean approved, String reason) {
}
