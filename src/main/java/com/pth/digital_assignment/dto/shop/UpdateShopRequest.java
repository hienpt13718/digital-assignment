package com.pth.digital_assignment.dto.shop;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalTime;

@Data
public class UpdateShopRequest {
    @Size(max = 100, message = "Shop name must not exceed 100 characters")
    private String name;

    private String address;

    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String phone;

    private Double latitude;

    private Double longitude;

    @Min(value = 1, message = "Maximum queue size must be at least 1")
    private Integer maxQueueSize;

    @Min(value = 1, message = "Number of queues must be at least 1")
    private Integer numberOfQueues;

    @Min(value = 1, message = "Opening day of week must be at least 1")
    private Integer openingDayOfWeek;

    private LocalTime openingTime;

    private LocalTime closingTime;
}
