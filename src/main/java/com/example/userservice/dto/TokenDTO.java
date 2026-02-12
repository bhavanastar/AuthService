package com.example.userservice.dto;

import com.example.userservice.models.Token;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TokenDTO {
    private String tokenValue;
    private Date expiryAt;
    private String email;

    public static TokenDTO from(Token token) {
        if(token == null) return null;

        TokenDTO tokenDTO = new TokenDTO();
        tokenDTO.setTokenValue(token.getTokenValue());
        tokenDTO.setEmail(token.getUser().getEmail());
        tokenDTO.setExpiryAt(token.getExpiryAt());

        return tokenDTO;
    }
}
