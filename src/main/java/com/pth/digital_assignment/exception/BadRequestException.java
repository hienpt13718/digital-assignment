package com.pth.digital_assignment.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {
    private final String errorCode;

    public BadRequestException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public static BadRequestException invalidQueueNum() {
        return new BadRequestException("INVALID_QUEUE_NUMBER", "Invalid queue number");
    }

    public static BadRequestException queueIsFull() {
        return new BadRequestException("QUEUE_IS_FULL", "Shop queue is full");
    }

    public static BadRequestException shopIsClosed() {
        return new BadRequestException("SHOP_IS_CLOSED" ,"Shop is currently closed");
    }

    public static BadRequestException menuItemIsNotAvailable(String name) {
        return new BadRequestException("MENU_ITEM_IS_NOT_AVAILABLE" ,"Menu item " + name + " is not available");
    }

    public static BadRequestException invalidOrderStatusForCancelling() {
        return new BadRequestException("ORDER_CANT_CANCEL" , "Order cannot be cancelled in current status");
    }
}
