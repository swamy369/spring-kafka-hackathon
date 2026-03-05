package com.hackathon.inventoryservice.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackathon.inventoryservice.event.InventoryResultEvent;
import com.hackathon.inventoryservice.event.OrderCreatedEvent;
import com.hackathon.inventoryservice.service.InventoryValidationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class InventoryEventListener {
    private static final String INVENTORY_EVENTS_TOPIC = "inventory-events";

    private final ObjectMapper objectMapper;
    private final InventoryValidationService inventoryValidationService;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public InventoryEventListener(
            ObjectMapper objectMapper,
            InventoryValidationService inventoryValidationService,
            KafkaTemplate<String, String> kafkaTemplate) {
        this.objectMapper = objectMapper;
        this.inventoryValidationService = inventoryValidationService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "order-events", groupId = "inventory-service-group")
    public void consumeOrderCreated(String payload) throws JsonProcessingException {
        OrderCreatedEvent orderCreated = objectMapper.readValue(payload, OrderCreatedEvent.class);
        boolean approved = inventoryValidationService.isInventoryAvailable(orderCreated.amount());
        String reason = approved ? "InventoryApproved" : "InventoryRejected";

        InventoryResultEvent result = new InventoryResultEvent(orderCreated.orderId(), approved, reason);
        kafkaTemplate.send(INVENTORY_EVENTS_TOPIC, orderCreated.orderId(), objectMapper.writeValueAsString(result));
    }
}
