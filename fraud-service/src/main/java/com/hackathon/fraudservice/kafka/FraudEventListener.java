package com.hackathon.fraudservice.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackathon.fraudservice.event.FraudResultEvent;
import com.hackathon.fraudservice.event.OrderCreatedEvent;
import com.hackathon.fraudservice.service.FraudValidationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class FraudEventListener {
    private static final String FRAUD_EVENTS_TOPIC = "fraud-events";

    private final ObjectMapper objectMapper;
    private final FraudValidationService fraudValidationService;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public FraudEventListener(
            ObjectMapper objectMapper,
            FraudValidationService fraudValidationService,
            KafkaTemplate<String, String> kafkaTemplate) {
        this.objectMapper = objectMapper;
        this.fraudValidationService = fraudValidationService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "order-events", groupId = "fraud-service-group")
    public void consumeOrderCreated(String payload) throws JsonProcessingException {
        OrderCreatedEvent orderCreated = objectMapper.readValue(payload, OrderCreatedEvent.class);
        boolean approved = fraudValidationService.isAllowed(orderCreated.amount());
        String reason = approved ? "FraudApproved" : "FraudRejected";

        FraudResultEvent result = new FraudResultEvent(orderCreated.orderId(), approved, reason);
        kafkaTemplate.send(FRAUD_EVENTS_TOPIC, orderCreated.orderId(), objectMapper.writeValueAsString(result));
    }
}
