package com.pth.digital_assignment.dto.shop;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalTime;

@Data
public class CreateShopRequest {
    @NotBlank(message = "Shop name is required")
    @Size(max = 100, message = "Shop name must not exceed 100 characters")
    private String name;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Phone number is required")
    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String phone;

    @NotNull(message = "Latitude is required")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    private Double longitude;

    @NotNull(message = "Maximum queue size is required")
    @Min(value = 1, message = "Maximum queue size must be at least 1")
    private Integer maxQueueSize;

    @NotNull(message = "Number of queues is required")
    @Min(value = 1, message = "Number of queues must be at least 1")
    private Integer numberOfQueues;

    @NotNull(message = "Opening day of week is required")
    @Min(value = 1, message = "Opening day of week must be at least 1")
    private Integer openingDayOfWeek;

    @NotNull(message = "Opening time is required")
    private LocalTime openingTime;

    @NotNull(message = "Closing time is required")
    private LocalTime closingTime;
}
