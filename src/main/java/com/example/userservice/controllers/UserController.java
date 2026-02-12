package com.example.userservice.controllers;

import com.example.userservice.dto.LoginRequestDTO;
import com.example.userservice.dto.SignupRequestDTO;
import com.example.userservice.dto.TokenDTO;
import com.example.userservice.dto.UserDTO;
import com.example.userservice.exceptions.InvalidTokenException;
import com.example.userservice.exceptions.PasswordMismatchException;
import com.example.userservice.models.Token;
import com.example.userservice.models.User;
import com.example.userservice.services.UserService;
import org.springframework.web.bind.annotation.*;

import java.security.Signature;

@RestController
@RequestMapping("/users")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public UserDTO signup(@RequestBody SignupRequestDTO requestDTO) {
        User user = userService.signup(
                requestDTO.getName(),
                requestDTO.getEmail(),
                requestDTO.getPassword());

        return UserDTO.from(user);
    }

    @PostMapping("/login")
    public TokenDTO login(@RequestBody LoginRequestDTO requestDTO) throws PasswordMismatchException {
        Token token = userService.login(requestDTO.getEmail(), requestDTO.getPassword());
        return TokenDTO.from(token);
    }

    @GetMapping("/validate/{tokenValue}")
    public UserDTO validateToken(@PathVariable String tokenValue) throws InvalidTokenException {
        User user = userService.validateToken(tokenValue);
        return UserDTO.from(user);
    }

    @PostMapping("/logout/{tokenValue}")
    public Boolean logout(@PathVariable String tokenValue) {
        return null;
    }
}
