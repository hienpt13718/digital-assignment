package com.pth.digital_assignment.dto.order;

import com.pth.digital_assignment.enums.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderStatusDto {
    private Long orderId;
    private OrderStatus status;
    private int queuePosition;
    private int estimatedWaitTimeMinutes;
    private BigDecimal totalAmount;
    private List<OrderItemDto> items;
    private ShopBasicInfo shop;
}
