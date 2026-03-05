package com.hackathon.inventoryservice.event;

public record InventoryResultEvent(String orderId, boolean approved, String reason) {
}
