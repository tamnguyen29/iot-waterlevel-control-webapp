package com.nvt.iot.exception;

import org.springframework.validation.BindingResult;

public class ValidationCustomException extends RuntimeException {
    private final String message;

    public ValidationCustomException(BindingResult bindingResult) {
        this.message = bindingResult.getFieldErrors().get(0).getDefaultMessage();
    }

    public ValidationCustomException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
