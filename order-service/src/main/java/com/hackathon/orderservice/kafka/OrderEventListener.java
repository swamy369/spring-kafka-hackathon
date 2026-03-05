package com.hackathon.orderservice.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackathon.orderservice.event.FraudResultEvent;
import com.hackathon.orderservice.event.InventoryResultEvent;
import com.hackathon.orderservice.service.OrderService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventListener {
    private final ObjectMapper objectMapper;
    private final OrderService orderService;

    public OrderEventListener(ObjectMapper objectMapper, OrderService orderService) {
        this.objectMapper = objectMapper;
        this.orderService = orderService;
    }

    @KafkaListener(topics = "inventory-events", groupId = "order-service-group")
    public void consumeInventoryResult(String payload) throws JsonProcessingException {
        InventoryResultEvent event = objectMapper.readValue(payload, InventoryResultEvent.class);
        orderService.updateInventoryDecision(event.orderId(), event.approved());
    }

    @KafkaListener(topics = "fraud-events", groupId = "order-service-group")
    public void consumeFraudResult(String payload) throws JsonProcessingException {
        FraudResultEvent event = objectMapper.readValue(payload, FraudResultEvent.class);
        orderService.updateFraudDecision(event.orderId(), event.approved());
    }
}
