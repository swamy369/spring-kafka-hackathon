package com.hackathon.orderservice.event;

public record InventoryResultEvent(String orderId, boolean approved, String reason) {
}
