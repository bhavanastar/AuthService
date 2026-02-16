package com.example.userservice.services;

import com.example.userservice.exceptions.InvalidTokenException;
import com.example.userservice.exceptions.PasswordMismatchException;
import com.example.userservice.models.Token;
import com.example.userservice.models.User;
import com.example.userservice.repositories.TokenRepository;
import com.example.userservice.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private TokenRepository tokenRepository;
    private SecretKey secretKey;

    private UserServiceImpl(UserRepository userRepository,
                            BCryptPasswordEncoder bCryptPasswordEncoder,
                            TokenRepository tokenRepository,
                            SecretKey secretKey) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.tokenRepository = tokenRepository;
        this.secretKey = secretKey;
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
    public Token loginOld(String email, String password) throws PasswordMismatchException {
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

    public String login(String email, String password) throws PasswordMismatchException {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isEmpty()) {
            return null;
        }

        User user = optionalUser.get();

        if(!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new PasswordMismatchException("Wrong password");
        }

        //Generate token

        Map<String, Object> claims = new HashMap<>();
        claims.put("iss", "home");
        claims.put("userId", user.getId());

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 30);
        Date expiryDate = calendar.getTime();

        claims.put("exp", expiryDate.getTime());
        claims.put("roles", user.getRoles());

        MacAlgorithm macAlgorithm = Jwts.SIG.HS256;
        //SecretKey secretKey = macAlgorithm.key().build();

        String token = Jwts.builder().setClaims(claims).signWith(secretKey).compact();
        return token;

    }

    public User validateTokenOld(String tokenValue) throws InvalidTokenException {
        Optional<Token> optionalToken = tokenRepository.findByTokenValueAndExpiryAtGreaterThan(tokenValue,
                new Date());
        if(optionalToken.isEmpty()) {
            throw new InvalidTokenException("Invalid token");
        }
        Token token = optionalToken.get();
        return token.getUser();
    }

    public User validateToken(String tokenValue) throws InvalidTokenException {
        JwtParser jwtParser = Jwts.parser().verifyWith(secretKey).build();
        Claims claims = jwtParser.parseSignedClaims(tokenValue).getPayload();
/*
        Date expiryDate = (Date) claims.get("exp");
        if(expiryDate.before(new Date())) {
            throw new InvalidTokenException("Invalid token");
        }
*/
        Long expiryTime = (Long) claims.get("exp");
        Long currentTime = System.currentTimeMillis();

        if(expiryTime < currentTime) {
            throw new InvalidTokenException("Invalid token");
        }

        Long userId = ((Number) claims.get("userId")).longValue();

        Optional<User> optionalUser = userRepository.findById(userId);

        return optionalUser.get();

    }
}
