package com.nvt.iot.controller;

import com.nvt.iot.service.WaterTankConnectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.security.Principal;


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

    @MessageMapping("/connect-device/{deviceId}")
    public void connectToDevice(@DestinationVariable String deviceId, Principal user) {
        log.info("{/connect-device/{deviceId}}: DeviceId(" + deviceId + "), User(" + user.getName() + ")");
        waterTankConnectionService.connectToDevice(deviceId, user);
    }

    @MessageMapping("/stop-connect-device/{deviceId}")
    public void stopConnectToDevice(@DestinationVariable String deviceId, Principal user) {
        log.info("{/stop-connect-device/{deviceId}}: DeviceId(" + deviceId + "), User(" + user.getName() + ")");
        waterTankConnectionService.stopConnectToDevice(deviceId, user);
    }
}
