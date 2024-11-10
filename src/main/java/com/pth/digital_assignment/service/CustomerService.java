package com.pth.digital_assignment.service;

import com.pth.digital_assignment.dto.customer.CustomerProfileResponse;
import com.pth.digital_assignment.dto.customer.CustomerRegistrationRequest;
import com.pth.digital_assignment.dto.customer.UpdateCustomerProfileRequest;

import java.util.UUID;

public interface CustomerService {
    CustomerProfileResponse registerCustomer(CustomerRegistrationRequest request);
    CustomerProfileResponse getCustomerProfile(String customerId);
    CustomerProfileResponse updateCustomerProfile(String customerId,
                                                  UpdateCustomerProfileRequest request);
    void incrementLoyaltyScore(UUID customerId);
}
