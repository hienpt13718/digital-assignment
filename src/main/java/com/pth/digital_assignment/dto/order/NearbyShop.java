package com.pth.digital_assignment.dto.order;

import lombok.Data;

import java.time.LocalTime;

@Data
public class NearbyShop {
    private Long id;
    private String name;
    private String address;
    private String contactNumber;
    private double latitude;
    private double longitude;
    private double distanceInKm;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private boolean isOpen;
    private int currentQueueSize;
    private int estimatedWaitTimeMinutes;
}
