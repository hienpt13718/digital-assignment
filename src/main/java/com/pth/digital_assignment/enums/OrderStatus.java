package com.pth.digital_assignment.enums;

import com.pth.digital_assignment.exception.BadRequestException;

public enum OrderStatus {
    PENDING,
    IN_QUEUE,
    PROCESSING,
    COMPLETED,
    CANCELLED;

    public static OrderStatus of(String status) {
        for (OrderStatus orderStatus : OrderStatus.values()) {
            if (orderStatus.name().equalsIgnoreCase(status)) {
                return orderStatus;
            }
        }

        throw new BadRequestException("INVALID_ORDER_STATUS", "Invalid order status");
    }
}
