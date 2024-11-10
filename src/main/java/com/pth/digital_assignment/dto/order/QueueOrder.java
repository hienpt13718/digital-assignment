package com.pth.digital_assignment.dto.order;

import com.pth.digital_assignment.enums.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class QueueOrder {
    private Long orderId;
    private int queuePosition;
    private CustomerInfo customer;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private List<OrderItemDto> items;
    private Double totalAmount;
}
