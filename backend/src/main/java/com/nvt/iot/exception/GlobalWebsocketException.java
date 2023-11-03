package com.nvt.iot.exception;

import com.nvt.iot.payload.response.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalWebsocketException {
    @Value("${websocket.room.private-error}")
    private String ERROR_ROOM;

    private final SimpMessagingTemplate messagingTemplate;
    @MessageExceptionHandler(WebsocketResourcesNotFoundException.class)
    public void handleWebsocketNotFoundException(WebsocketResourcesNotFoundException e) {
        var errorMessage = ErrorResponse.builder()
            .message(e.getMessage())
            .statusCode(404)
            .build();
        messagingTemplate.convertAndSendToUser(e.getClientName(), ERROR_ROOM, errorMessage);
    }

    @MessageExceptionHandler(WebsocketValidationException.class)
    public void handleWebsocketValidationException(WebsocketValidationException e) {
        var errorMessage = ErrorResponse.builder()
            .message(e.getMessage())
            .statusCode(400)
            .build();
        messagingTemplate.convertAndSendToUser(e.getClientName(), ERROR_ROOM, errorMessage);
    }
}
