package com.pth.digital_assignment.dto.shop;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class ShopResponse {
    private Long id;
    private String name;
    private String address;
    private String phone;
    private Double latitude;
    private Double longitude;
    private Integer maxQueueSize;
    private Integer numberOfQueues;
    private LocalDateTime createdAt;
    private Integer openingDayOfWeek;
    private LocalTime openingTime;
    private LocalTime closingTime;
}
