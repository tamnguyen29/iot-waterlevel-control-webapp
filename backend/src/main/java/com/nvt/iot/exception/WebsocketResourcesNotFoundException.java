package com.nvt.iot.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class WebsocketResourcesNotFoundException extends RuntimeException{
    private String message;
    private String clientName;

    @Override
    public String getMessage() {
        return message;
    }

    public String getClientName() {
        return clientName;
    }
}
