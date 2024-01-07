package com.nvt.iot.controller;

import com.nvt.iot.payload.request.UserUpdateInformationRequest;
import com.nvt.iot.payload.response.BaseResponse;
import com.nvt.iot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<?> getAllUsers(
        @RequestParam(defaultValue = "0") Integer pageNo,
        @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        var response = BaseResponse.builder()
            .statusCode(200)
            .message("Get all users successfully!")
            .data(userService.getAllUsers(pageNo, pageSize))
            .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable String id) {
        var response = BaseResponse.builder()
            .statusCode(200)
            .message("Get user with id " + id + " successfully!")
            .data(userService.getUser(id))
            .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        var response = BaseResponse.builder()
            .statusCode(200)
            .message("Delete successfully user with id: " + id)
            .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/update-info/{id}")
    public ResponseEntity<?> updateUser(
        @PathVariable String id,
        @RequestBody @Valid UserUpdateInformationRequest userRequest,
        BindingResult bindingResult
    ) {
        var response = BaseResponse.builder()
            .statusCode(200)
            .data(userService.updateInfoUser(id, userRequest, bindingResult))
            .message("Update successfully!")
            .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/amount")
    public ResponseEntity<?> getAmountOfUsers() {
        var response = BaseResponse.builder()
            .message("Get amount of user successfully!")
            .statusCode(200)
            .data(userService.getTotalAmount())
            .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
