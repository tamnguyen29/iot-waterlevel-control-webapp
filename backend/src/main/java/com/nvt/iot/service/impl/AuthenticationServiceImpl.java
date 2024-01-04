package com.nvt.iot.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nvt.iot.document.UserDocument;
import com.nvt.iot.exception.AuthenticationCustomException;
import com.nvt.iot.exception.EmailAlreadyExistsException;
import com.nvt.iot.exception.ValidationCustomException;
import com.nvt.iot.mapper.UserDTOMapper;
import com.nvt.iot.model.Action;
import com.nvt.iot.model.Role;
import com.nvt.iot.model.UsingStatus;
import com.nvt.iot.payload.request.AuthenticationRequest;
import com.nvt.iot.payload.request.RegisterRequest;
import com.nvt.iot.payload.response.AuthenticationResponse;
import com.nvt.iot.payload.response.RegisterResponse;
import com.nvt.iot.repository.ConnectedDeviceRepository;
import com.nvt.iot.repository.ConnectedUserRepository;
import com.nvt.iot.repository.UserRepository;
import com.nvt.iot.service.AuthenticationService;
import com.nvt.iot.service.JwtService;
import com.nvt.iot.service.WebsocketHandleEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDTOMapper userDTOMapper;
    private final ConnectedUserRepository connectedUserRepository;
    private final ConnectedDeviceRepository connectedDeviceRepository;
    private final WebsocketHandleEventService websocketHandleEventService;

    @Override
    public RegisterResponse register(RegisterRequest registerRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationCustomException(bindingResult);
        }

        Optional<UserDocument> userOptional = userRepository.findByEmail(registerRequest.getEmail());
        if (userOptional.isPresent()) {
            throw new EmailAlreadyExistsException("Email already existed!");
        }

        var user = UserDocument.builder()
            .email(registerRequest.getEmail())
            .fullName(registerRequest.getFullName())
            .password(passwordEncoder.encode(registerRequest.getPassword()))
            .role(Role.ROLE_USER)
            .createAt(new Date(System.currentTimeMillis()))
            .updatedAt(new Date(System.currentTimeMillis()))
            .build();
        userRepository.save(user);
        return RegisterResponse.builder()
            .user(userDTOMapper.apply(user))
            .build();
    }

    @Override
    public AuthenticationResponse authenticate(
        AuthenticationRequest authenticationRequest,
        BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new ValidationCustomException(bindingResult);
        }

        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                authenticationRequest.getEmail(),
                authenticationRequest.getPassword()
            )
        );
        Optional<UserDocument> userOptional = userRepository.findByEmail(authenticationRequest.getEmail());
        if (userOptional.isEmpty()) {
            throw new AuthenticationCustomException("Invalid email!");
        }

        UserDocument userDocument = userOptional.get();
        var jwtToken = jwtService.generateToken(userDocument);
        var jwtRefreshToken = jwtService.generateRefreshToken(userDocument);

        return AuthenticationResponse.builder()
            .user(userDTOMapper.apply(userDocument))
            .accessToken(jwtToken)
            .refreshToken(jwtRefreshToken)
            .build();
    }

    @Override
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        System.out.println(authHeader);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        String refreshToken = authHeader.substring(7);
        String userEmail = jwtService.extractUsername(refreshToken);

        if (userEmail != null) {
            UserDetails user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new AuthenticationCustomException("Can not find email in refresh token!"));
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                var authResponse = AuthenticationResponse.builder()
                    .refreshToken(refreshToken)
                    .accessToken(accessToken)
                    .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

    @Override
    public void logout(String userId) {
        connectedUserRepository.findById(userId)
            .ifPresent((connectedUserDocument -> {
                connectedUserRepository.deleteById(userId);
                connectedDeviceRepository.findByCurrentUsingUserId(
                    connectedUserDocument.getId()
                ).ifPresent((deviceDocument -> {
                    deviceDocument.setUsingStatus(UsingStatus.AVAILABLE);
                    deviceDocument.setCurrentUsingUser(null);
                    connectedDeviceRepository.save(deviceDocument);
                    websocketHandleEventService.sendMessageToDevice(
                        userId,
                        deviceDocument.getId(),
                        Action.USER_DISCONNECT_TO_DEVICE
                    );
                    websocketHandleEventService.sendListDeviceToAllUser();
                }));
                websocketHandleEventService.sendListUserToAllUser();
            }));
    }
}
