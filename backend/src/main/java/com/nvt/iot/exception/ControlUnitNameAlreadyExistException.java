package com.nvt.iot.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ControlUnitNameAlreadyExistException extends RuntimeException {
    private String message;

    public String getMessage() {
        return message;
    }
}
