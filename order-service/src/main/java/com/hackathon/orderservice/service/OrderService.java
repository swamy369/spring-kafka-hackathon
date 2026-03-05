package com.hackathon.orderservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackathon.orderservice.dto.CreateOrderRequest;
import com.hackathon.orderservice.event.OrderCreatedEvent;
import com.hackathon.orderservice.model.DecisionStatus;
import com.hackathon.orderservice.model.Order;
import com.hackathon.orderservice.model.OrderStatus;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private static final String ORDER_EVENTS_TOPIC = "order-events";

    private final Map<String, Order> orderStore = new ConcurrentHashMap<>();
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public OrderService(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public Order createOrder(CreateOrderRequest request) {
        String orderId = UUID.randomUUID().toString();
        Order order = new Order(orderId, request.customerId(), request.amount());
        orderStore.put(orderId, order);

        OrderCreatedEvent event = new OrderCreatedEvent(orderId, request.customerId(), request.amount());
        try {
            kafkaTemplate.send(ORDER_EVENTS_TOPIC, orderId, objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to publish OrderCreated event for " + orderId, ex);
        }
        return order;
    }

    public Optional<Order> getOrder(String orderId) {
        return Optional.ofNullable(orderStore.get(orderId));
    }

    public void updateInventoryDecision(String orderId, boolean approved) {
        Order order = orderStore.get(orderId);
        if (order == null) {
            return;
        }
        order.setInventoryStatus(approved ? DecisionStatus.APPROVED : DecisionStatus.REJECTED);
        updateFinalStatus(order);
    }

    public void updateFraudDecision(String orderId, boolean approved) {
        Order order = orderStore.get(orderId);
        if (order == null) {
            return;
        }
        order.setFraudStatus(approved ? DecisionStatus.APPROVED : DecisionStatus.REJECTED);
        updateFinalStatus(order);
    }

    private void updateFinalStatus(Order order) {
        if (order.getInventoryStatus() == DecisionStatus.REJECTED
                || order.getFraudStatus() == DecisionStatus.REJECTED) {
            order.setStatus(OrderStatus.REJECTED);
            return;
        }

        if (order.getInventoryStatus() == DecisionStatus.APPROVED
                && order.getFraudStatus() == DecisionStatus.APPROVED) {
            order.setStatus(OrderStatus.APPROVED);
        }
    }
}
