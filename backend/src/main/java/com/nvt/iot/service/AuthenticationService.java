package com.nvt.iot.service;

import com.nvt.iot.payload.request.AuthenticationRequest;
import com.nvt.iot.payload.request.RegisterRequest;
import com.nvt.iot.payload.response.AuthenticationResponse;
import com.nvt.iot.payload.response.RegisterResponse;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface AuthenticationService {
    AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest, BindingResult bindingResult);
    RegisterResponse register(RegisterRequest registerRequest, BindingResult bindingResult);
    void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
