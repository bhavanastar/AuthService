package com.example.userservice.services;

import com.example.userservice.exceptions.InvalidTokenException;
import com.example.userservice.exceptions.PasswordMismatchException;
import com.example.userservice.models.Token;
import com.example.userservice.models.User;
import com.example.userservice.repositories.TokenRepository;
import com.example.userservice.repositories.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private TokenRepository tokenRepository;

    private UserServiceImpl(UserRepository userRepository,
                            BCryptPasswordEncoder bCryptPasswordEncoder,
                            TokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.tokenRepository = tokenRepository;
    }

    @Override
    public User signup(String name, String email, String password) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isPresent()) {
            return optionalUser.get();
        }
        User user = new User();
        user.setName(name);
        user.setEmail(email);

        user.setPassword(bCryptPasswordEncoder.encode(password));
        userRepository.save(user);
        return user;
    }

    @Override
    public Token login(String email, String password) throws PasswordMismatchException {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isEmpty()) {
            return null;
        }

        User user = optionalUser.get();

        if(!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new PasswordMismatchException("Wrong password");
        }

        //Generate token

        Token token = new Token();
        token.setUser(user);
        token.setTokenValue(RandomStringUtils.randomAlphanumeric(128));

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 30);
        Date expiryDate = calendar.getTime();
        token.setExpiryAt(expiryDate);

        return tokenRepository.save(token);


    }

    @Override
    public User validateToken(String tokenValue) throws InvalidTokenException {
        Optional<Token> optionalToken = tokenRepository.findByTokenValueAndExpiryAtGreaterThan(tokenValue,
                new Date());
        if(optionalToken.isEmpty()) {
            throw new InvalidTokenException("Invalid token");
        }
        Token token = optionalToken.get();
        return token.getUser();
    }
}
