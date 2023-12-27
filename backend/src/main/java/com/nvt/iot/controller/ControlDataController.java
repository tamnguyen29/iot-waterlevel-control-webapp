package com.nvt.iot.controller;

import com.nvt.iot.payload.response.BaseResponse;
import com.nvt.iot.service.ControlDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/data-control")
@RequiredArgsConstructor
public class ControlDataController {
    private final ControlDataService controlDataService;

    @GetMapping
    public ResponseEntity<?> getDataControl(@RequestParam String userId) {
        var response = BaseResponse.builder()
            .statusCode(200)
            .data(controlDataService.getAllControlDataByUserId(userId))
            .message("Get control data successfully!")
            .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
