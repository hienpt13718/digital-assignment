package com.pth.digital_assignment.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {
    @NotNull
    private Long shopId; // require

    @NotNull
    @Size(min = 1)
    private List<@Valid OrderItemRequest> items;
}
