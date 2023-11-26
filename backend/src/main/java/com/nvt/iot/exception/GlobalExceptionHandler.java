package com.nvt.iot.exception;

import com.nvt.iot.payload.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundCustomException.class)
    public ResponseEntity<?> handleCustomNotFoundException(Exception e) {
        var response = ErrorResponse.builder()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .message(e.getMessage())
            .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(response);
    }

    @ExceptionHandler(AuthenticationCustomException.class)
    public ResponseEntity<?> handleAuthenticationCustomException(Exception e) {
        var response = ErrorResponse.builder()
            .statusCode(HttpStatus.UNAUTHORIZED.value())
            .message(e.getMessage())
            .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(response);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<?> handleEmailAlreadyExistsException(Exception e) {
        var response = ErrorResponse.builder()
            .statusCode(HttpStatus.CONFLICT.value())
            .message(e.getMessage())
            .build();
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(response);
    }

    @ExceptionHandler(ValidationCustomException.class)
    public ResponseEntity<?> handleValidationCustomException(Exception e) {
        var response = ErrorResponse.builder()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .message(e.getMessage())
            .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(response);
    }

    @ExceptionHandler(DeviceNameAlreadyExistException.class)
    public ResponseEntity<?> handleDeviceNameAlreadyExistException(Exception e) {
        var response = ErrorResponse.builder()
            .statusCode(HttpStatus.CONFLICT.value())
            .message(e.getMessage())
            .build();
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(response);
    }

    @ExceptionHandler(InvalidProcessValueException.class)
    public ResponseEntity<?> handleInvalidProcessValueException(Exception e) {
        var response = ErrorResponse.builder()
            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .message(e.getMessage())
            .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(response);
    }
}
