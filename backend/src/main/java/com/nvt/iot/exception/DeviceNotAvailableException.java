package com.nvt.iot.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DeviceNotAvailableException extends RuntimeException {
    private String message;

    @Override
    public String getMessage() {
        return message;
    }
}
