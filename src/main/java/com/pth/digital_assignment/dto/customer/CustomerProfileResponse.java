package com.pth.digital_assignment.dto.customer;

import lombok.Data;

import java.util.UUID;

@Data
public class CustomerProfileResponse {
    private UUID id;
    private String name;
    private String phone;
    private String address;
    private Integer loyaltyScore;
}
