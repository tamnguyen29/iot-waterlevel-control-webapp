package com.nvt.iot.controller;

import com.nvt.iot.service.WaterTankConnectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;


@Controller
@RequiredArgsConstructor
@Slf4j
public class WaterTankConnectionController {
    private final WaterTankConnectionService waterTankConnectionService;

    @MessageMapping("/member-connect")
    public void getUsersConnect(SimpMessageHeaderAccessor headerAccessor) {
        log.info("{/member-connect}: " + headerAccessor.getSessionAttributes());
        waterTankConnectionService.sendListMemberToEveryUserClient(headerAccessor);
    }

    @MessageMapping("/ping")
    public String handlePing() {
        return "PONG";
    }
}
