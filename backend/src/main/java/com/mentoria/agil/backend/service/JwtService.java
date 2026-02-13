package com.mentoria.agil.backend.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.mentoria.agil.backend.service.TokenBlacklistService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.util.Date;

@Service
public class JwtService {

    @Value("${api.security.token.secret:my-secret-key}")
    private String secret;
    
    @Value("${api.security.token.issuer:auth-api}")
    private String issuer;

    private final TokenBlacklistService tokenBlacklistService;
    
    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(secret);
    }
}