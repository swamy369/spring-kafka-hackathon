package com.hackathon.orderservice.model;

public class Order {
    private String orderId;
    private String customerId;
    private double amount;
    private OrderStatus status;
    private DecisionStatus inventoryStatus;
    private DecisionStatus fraudStatus;

    public Order(String orderId, String customerId, double amount) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.amount = amount;
        this.status = OrderStatus.PENDING;
        this.inventoryStatus = DecisionStatus.PENDING;
        this.fraudStatus = DecisionStatus.PENDING;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public double getAmount() {
        return amount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public DecisionStatus getInventoryStatus() {
        return inventoryStatus;
    }

    public void setInventoryStatus(DecisionStatus inventoryStatus) {
        this.inventoryStatus = inventoryStatus;
    }

    public DecisionStatus getFraudStatus() {
        return fraudStatus;
    }

    public void setFraudStatus(DecisionStatus fraudStatus) {
        this.fraudStatus = fraudStatus;
    }
}
