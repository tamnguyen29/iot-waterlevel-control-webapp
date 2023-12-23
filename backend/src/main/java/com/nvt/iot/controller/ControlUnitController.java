package com.nvt.iot.controller;

import com.nvt.iot.payload.request.ControlUnitRequest;
import com.nvt.iot.payload.response.BaseResponse;
import com.nvt.iot.service.ControlUnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/unit-control")
@RequiredArgsConstructor
public class ControlUnitController {
    private final ControlUnitService controlUnitService;

    @PostMapping("/add")
    public ResponseEntity<?> addUnitController(
        @RequestBody @Valid ControlUnitRequest controlUnitRequest,
        BindingResult bindingResult
    ) {
        controlUnitService.addUnitControl(controlUnitRequest, bindingResult);
        var response = BaseResponse.builder()
            .statusCode(201)
            .message("Add unit control successfully!")
            .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<?> getAllUnitControllersByUserId(@RequestParam String userId) {
        var response = BaseResponse.builder()
            .statusCode(200)
            .message("Get all unit controller successfully!")
            .data(controlUnitService.getAllUnitControlsByUserId(userId))
            .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUnitController(@PathVariable String id) {
        var response = BaseResponse.builder()
            .statusCode(200)
            .message("Get unit control with id " + id + " successfully!")
            .data(controlUnitService.getUnitControlById(id))
            .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUnitControl(
        @PathVariable String id,
        @RequestBody @Valid ControlUnitRequest controlUnitRequest,
        BindingResult bindingResult
    ) {
        controlUnitService.update(id, controlUnitRequest, bindingResult);
        var response = BaseResponse.builder()
            .statusCode(200)
            .message("Update control unit successfully!")
            .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        controlUnitService.delete(id);
        var response = BaseResponse.builder()
            .statusCode(200)
            .message("Delete control unit successfully!")
            .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
