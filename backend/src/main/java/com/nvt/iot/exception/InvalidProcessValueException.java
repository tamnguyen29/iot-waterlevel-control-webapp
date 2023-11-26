package com.nvt.iot.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InvalidProcessValueException extends RuntimeException {
    private String message;

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
