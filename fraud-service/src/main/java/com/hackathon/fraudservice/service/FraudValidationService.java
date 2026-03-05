package com.hackathon.fraudservice.service;

import org.springframework.stereotype.Service;

@Service
public class FraudValidationService {
    private static final double FRAUD_THRESHOLD = 50000.0;

    public boolean isAllowed(double amount) {
        return amount <= FRAUD_THRESHOLD;
    }
}
