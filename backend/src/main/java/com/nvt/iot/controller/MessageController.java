package com.nvt.iot.controller;

import com.nvt.iot.model.Message;
import com.nvt.iot.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Date;
import java.util.Objects;


@Controller
@RequiredArgsConstructor
@Slf4j
public class MessageController {
    private final MessageService messageService;

    @MessageMapping("/member-connect")
    public void getUsersConnect(SimpMessageHeaderAccessor headerAccessor) {
        messageService.sendListMemberToEveryUserClient(headerAccessor);
    }

    @MessageMapping("/connect-device/{deviceId}")
    public Message connectToDevice(@DestinationVariable String deviceId, Principal user) {
        messageService.connectToDevice(deviceId, user);
        return Message.builder()
            .content("Hello /connect-device/{deviceId}")
            .time(new Date(System.currentTimeMillis()))
            .build();
    }
}
