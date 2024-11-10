package com.pth.digital_assignment.dto.order;

import lombok.Data;

@Data
public class CustomerLocation {
    private double latitude;
    private double longitude;
    private double radiusInKm;
}
