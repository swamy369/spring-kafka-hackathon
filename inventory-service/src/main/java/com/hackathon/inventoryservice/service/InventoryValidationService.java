package com.hackathon.inventoryservice.service;

import org.springframework.stereotype.Service;

@Service
public class InventoryValidationService {
    private static final double INVENTORY_APPROVAL_LIMIT = 90000.0;

    public boolean isInventoryAvailable(double amount) {
        return amount <= INVENTORY_APPROVAL_LIMIT;
    }
}
