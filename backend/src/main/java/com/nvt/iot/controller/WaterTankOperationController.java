package com.nvt.iot.controller;

import com.nvt.iot.payload.response.BaseResponse;
import com.nvt.iot.service.WaterTankOperationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/operation")
public class WaterTankOperationController {
    private final WaterTankOperationService waterTankOperationService;

    @GetMapping("/start-measurement/{deviceId}")
    public ResponseEntity<?> startMeasurement(
        @PathVariable String deviceId,
        @RequestParam String controlUnitId,
        @RequestParam String userId
    ) {
        waterTankOperationService.startMeasurement(controlUnitId, deviceId, userId);
        var response = BaseResponse.builder()
            .statusCode(200)
            .message("Start measurement successfully!")
            .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/stop-measurement/{deviceId}")
    public ResponseEntity<?> startMeasurement(
        @PathVariable String deviceId,
        @RequestParam String userId
    ) {
        waterTankOperationService.stopMeasurement(deviceId, userId);
        var response = BaseResponse.builder()
            .statusCode(200)
            .message("Stop measurement successfully!")
            .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/reset-process/{deviceId}")
    public ResponseEntity<?> restartProcess(
        @PathVariable String deviceId,
        @RequestParam String userId
    ) {
        waterTankOperationService.sendRestartControlProcessToDevice(deviceId, userId);
        var response = BaseResponse.builder()
            .statusCode(200)
            .message("Restart control process successfully!")
            .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
