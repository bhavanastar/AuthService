package com.example.userservice.services;

import com.example.userservice.exceptions.InvalidTokenException;
import com.example.userservice.exceptions.PasswordMismatchException;
import com.example.userservice.models.Token;
import com.example.userservice.models.User;

public interface UserService {
    User signup(String name, String email, String password);

    Token login(String email, String password) throws PasswordMismatchException;

    User validateToken(String tokenValue) throws InvalidTokenException;

}
