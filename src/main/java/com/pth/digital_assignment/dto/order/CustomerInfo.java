package com.pth.digital_assignment.dto.order;

import lombok.Data;

import java.util.UUID;

@Data
public class CustomerInfo {
    private UUID customerId;
    private String name;
    private String phone;
    private int loyaltyScore;
}
