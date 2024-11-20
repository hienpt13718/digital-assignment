package com.pth.digital_assignment.dto.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderItemRequest {
    @NotNull
    private Long menuItemId;

    @Min(1)
    private int quantity;
}
