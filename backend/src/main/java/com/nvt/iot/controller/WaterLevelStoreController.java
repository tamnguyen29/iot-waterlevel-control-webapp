package com.nvt.iot.controller;

import com.nvt.iot.payload.response.BaseResponse;
import com.nvt.iot.service.WaterLevelStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/water-level")
@RequiredArgsConstructor
public class WaterLevelStoreController {
    private final WaterLevelStoreService waterLevelStoreService;

    @GetMapping
    public ResponseEntity<?> getAllWaterLevel(
        @RequestParam String userId,
        @RequestParam String deviceId,
        @RequestParam String controlUnitId
    ) {
        var response = BaseResponse.builder()
            .statusCode(200)
            .message("Get all water level data successfully!")
            .data(waterLevelStoreService.getAllData(userId, controlUnitId, deviceId))
            .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteWaterLevelData(
        @RequestParam String userId,
        @RequestParam String deviceId,
        @RequestParam String controlUnitId
    ) {
        waterLevelStoreService.deleteWaterLevelData(userId, controlUnitId, deviceId);
        var response = BaseResponse.builder()
            .statusCode(200)
            .message("Delete water level data successfully!")
            .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
