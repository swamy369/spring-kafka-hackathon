package com.hackathon.orderservice.dto;

public record CreateOrderRequest(String customerId, double amount) {
}
