package com.example.userservice.controlleradvices;

import com.example.userservice.dto.ExceptionDTO;
import com.example.userservice.exceptions.InvalidTokenException;
import com.example.userservice.exceptions.PasswordMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({InvalidTokenException.class, PasswordMismatchException.class})
    public ResponseEntity<ExceptionDTO> handleInvalidTokenException() {

        ExceptionDTO exceptionDTO = new ExceptionDTO();
        exceptionDTO.setMessage("Unauthorized access, please try again with correct credentials");
        return new ResponseEntity<>(
                exceptionDTO,
                HttpStatus.UNAUTHORIZED);
    }
}
