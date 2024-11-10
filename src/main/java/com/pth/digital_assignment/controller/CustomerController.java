package com.pth.digital_assignment.controller;

import com.pth.digital_assignment.dto.customer.CustomerProfileResponse;
import com.pth.digital_assignment.dto.customer.CustomerRegistrationRequest;
import com.pth.digital_assignment.dto.customer.UpdateCustomerProfileRequest;
import com.pth.digital_assignment.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping("/register")
    public ResponseEntity<CustomerProfileResponse> registerCustomer(
            @Valid @RequestBody CustomerRegistrationRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(customerService.registerCustomer(request));
    }

    @GetMapping("/profile")
    @PreAuthorize("hasAnyAuthority('CUSTOMER')")
    public ResponseEntity<CustomerProfileResponse> getCustomerProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                customerService.getCustomerProfile(userDetails.getUsername())
        );
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CustomerProfileResponse> updateCustomerProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateCustomerProfileRequest request) {
        return ResponseEntity.ok(
                customerService.updateCustomerProfile(userDetails.getUsername(), request)
        );
    }
}
