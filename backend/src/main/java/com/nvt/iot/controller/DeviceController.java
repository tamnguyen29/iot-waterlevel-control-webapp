package com.nvt.iot.controller;

import com.nvt.iot.model.DataFromDevice;
import com.nvt.iot.model.SignalControl;
import com.nvt.iot.payload.request.DeviceRequest;
import com.nvt.iot.payload.response.BaseResponse;
import com.nvt.iot.service.DeviceService;
import com.nvt.iot.service.WaterTankOperationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/device")
@RequiredArgsConstructor
public class DeviceController {
    private final DeviceService deviceService;
    private final WaterTankOperationService waterTankOperationService;

    @PostMapping("/add")
    public ResponseEntity<?> addDevice(
        @RequestBody @Valid DeviceRequest deviceRequest,
        BindingResult bindingResult
    ) {
        var response = BaseResponse.builder()
            .statusCode(201)
            .data(deviceService.add(deviceRequest, bindingResult))
            .message("Add device successfully!")
            .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<?> getAllDevices(
        @RequestParam(defaultValue = "0") Integer pageNo,
        @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        var response = BaseResponse.builder()
            .statusCode(200)
            .message("Get all devices successfully!")
            .data(deviceService.getAllDevices(pageNo, pageSize))
            .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateDevice(
        @PathVariable String id,
        @RequestBody @Valid DeviceRequest deviceRequest,
        BindingResult bindingResult
    ) {
        deviceService.update(id, deviceRequest, bindingResult);
        var response = BaseResponse.builder()
            .statusCode(200)
            .message("Update successfully!")
            .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteDevice(@PathVariable String id) {
        deviceService.delete(id);
        var response = BaseResponse.builder()
            .statusCode(200)
            .message("Delete successfully!")
            .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/send-data")
    public ResponseEntity<?> sendData(@RequestBody DataFromDevice data) {
        System.out.println("/send-data: " + data);
        long time = System.currentTimeMillis();
        SignalControl sigNalControl = waterTankOperationService.getWaterLevelDataFromDevice(data);
        System.out.println("time: " + (System.currentTimeMillis() - time) * 1.0f / 1000);
        return new ResponseEntity<>(sigNalControl, HttpStatus.OK);
    }

    @PostMapping("/send-first-data")
    public ResponseEntity<?> sendFirstData(@RequestBody DataFromDevice data) {
        System.out.println("/send-data: " + data);
        double sigNalControl = waterTankOperationService.sendFirstData(data);
        return new ResponseEntity<>(sigNalControl, HttpStatus.OK);
    }
}
