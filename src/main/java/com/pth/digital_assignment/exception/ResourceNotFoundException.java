package com.pth.digital_assignment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends BadRequestException {
    public static final String ERROR_CODE = "RESOURCE_NOT_FOUND";

    public ResourceNotFoundException(String message) {
        super(ERROR_CODE, message);
    }

    public ResourceNotFoundException(String errorCode, String message) {
        super(errorCode, message);
    }

    public static ResourceNotFoundException shopNotFound(Long id) {
        return new ResourceNotFoundException("SHOP_NOT_FOUND", "Shop not found with id: " + id);
    }

    public static ResourceNotFoundException customerNotFound(UUID customerId) {
        return new ResourceNotFoundException("CUSTOMER_NOT_FOUND","Customer not found with id: " + customerId);
    }

    public static ResourceNotFoundException menuItemNotFound(Long id) {
        return new ResourceNotFoundException("MENU_ITEM_NOT_FOUND", "Menu item not found with id: " + id);
    }

    public static ResourceNotFoundException noOrderInQueue(int queueNum) {
        return new ResourceNotFoundException("QUEUE_IS_EMPTY", "No order in queue " + queueNum);
    }

    public static ResourceNotFoundException orderNotFound(Long orderId) {
        return new ResourceNotFoundException("ORDER_NOT_FOUND", "Order not found with id: " + orderId);
    }
}
