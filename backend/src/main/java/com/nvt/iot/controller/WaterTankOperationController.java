package com.nvt.iot.controller;

import com.nvt.iot.service.WaterTankOperationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WaterTankOperationController {
    private final WaterTankOperationService waterTankOperationService;

    @MessageMapping("/operation/start-measurement/{deviceId}")
    public void getStartMeasurementParameters(
        @DestinationVariable String deviceId,
        @Payload String controllerId,
        Principal user
    ) {
        log.info("{/operation/start-measurement/{deviceId}}: deviceId[" + deviceId + "] + " +
            "Controller id[" + controllerId + "], user[" + user.getName() + "]");
        waterTankOperationService.startMeasurementWithControlParameter(controllerId, deviceId, user.getName());
    }

    @MessageMapping("/operation/stop-measurement/{deviceId}")
    public void getStartMeasurementParameters(
        @DestinationVariable String deviceId,
        Principal user
    ) {
        log.info("{/operation/stop-measurement/{deviceId}}: deviceId[" + deviceId + "]" +
            ", user[" + user.getName() + "]");
        waterTankOperationService.stopMeasurement(deviceId, user.getName());
    }

}
