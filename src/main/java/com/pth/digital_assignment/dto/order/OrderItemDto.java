package com.pth.digital_assignment.dto.order;

import lombok.Data;

@Data
public class OrderItemDto {
    private Long menuItemId;
    private String itemName;
    private int quantity;
    private Double unitPrice;
}
