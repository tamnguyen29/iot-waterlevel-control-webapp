package com.nvt.iot.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AuthenticationCustomException extends RuntimeException{
    private String message;

    @Override
    public String getMessage() {
        return message;
    }
}
