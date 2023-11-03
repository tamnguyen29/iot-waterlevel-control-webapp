package com.nvt.iot.controller;

import com.nvt.iot.payload.request.UserRequest;
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

    @PostMapping("/add")
    public ResponseEntity<?> addYUser(
        @RequestBody @Valid UserRequest userRequest,
        BindingResult bindingResult
    ) {
        userService.addUser(userRequest, bindingResult);
        var response = BaseResponse.builder()
            .statusCode(201)
            .message("Add user successfully!")
            .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
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

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUser(
        @PathVariable String id,
        @RequestBody @Valid UserRequest userRequest,
        BindingResult bindingResult
    ) {
        userService.updateUser(id, userRequest, bindingResult);
        var response = BaseResponse.builder()
            .statusCode(200)
            .message("Update successfully!")
            .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
