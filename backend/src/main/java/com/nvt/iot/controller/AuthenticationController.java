package com.nvt.iot.controller;

import com.nvt.iot.payload.request.AuthenticationRequest;
import com.nvt.iot.payload.request.RegisterRequest;
import com.nvt.iot.payload.response.BaseResponse;
import com.nvt.iot.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    @PostMapping("/register")
    public ResponseEntity<?> register(
        @RequestBody @Valid RegisterRequest registerRequest,
        BindingResult bindingResult
    ) {
        var response = BaseResponse.builder()
            .statusCode(201)
            .message("Register successfully!")
            .data(authenticationService.register(registerRequest, bindingResult))
            .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(
        @RequestBody @Valid AuthenticationRequest authenticationRequest,
        BindingResult bindingResult
    ) {
        var response = BaseResponse.builder()
            .statusCode(200)
            .message("Authenticate successfully!")
            .data(authenticationService.authenticate(authenticationRequest, bindingResult))
            .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/refresh-token")
    public void refreshToken(
        HttpServletRequest request,
        HttpServletResponse response
    ) throws IOException {
        authenticationService.refreshToken(request, response);
    }
}
