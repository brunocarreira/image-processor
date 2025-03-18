package com.bix.processor.security;

import com.bix.processor.domain.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtAuthenticationResponse {

    private String accessToken;
    private String tokenType = "Bearer";
    private User user;

    public JwtAuthenticationResponse(String accessToken, User customer) {
        this.accessToken = accessToken;
        this.user = customer;
    }
}