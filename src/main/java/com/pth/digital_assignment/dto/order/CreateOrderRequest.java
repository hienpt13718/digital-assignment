package com.pth.digital_assignment.dto.order;

import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {
    private Long shopId;
    private List<OrderItemRequest> items;
}
