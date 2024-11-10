package com.pth.digital_assignment.dto.customer;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import static com.pth.digital_assignment.constant.Constants.PHONE_REGEXP;

@Data
public class UpdateCustomerProfileRequest {
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @Pattern(regexp = PHONE_REGEXP, message = "Invalid phone number format")
    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String phone;

    private String address;
}
