package com.pth.digital_assignment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ResourceDuplicatedException extends BadRequestException {
    public static final String ERROR_CODE = "RESOURCE_DUPLICATED";

    public ResourceDuplicatedException(String message) {
        super(ERROR_CODE, message);
    }

    public ResourceDuplicatedException(String errorCode, String message) {
        super(errorCode, message);
    }

    public static ResourceDuplicatedException customerPhoneDuplicated() {
        return new ResourceDuplicatedException("PHONE_NO_EXISTED", "Phone number already registered");
    }

    public static ResourceDuplicatedException usernameDuplicated() {
        return new ResourceDuplicatedException("USERNAME_EXISTED", "Username already exists");
    }
}
