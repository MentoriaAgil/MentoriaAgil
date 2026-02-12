package com.mentoria.agil.backend.service;

import com.okta.jwt.JwtVerificationException;

import com.mentoria.agil.backend.service.TokenBlacklistService;

@Service
public class JwtService {

    private static final TokenBlacklistService tokenBlacklistService;
    
    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(SECRET);
    }

    public boolean isValidToken(String token) {
    if (tokenBlacklistService.isTokenBlacklisted(token)) {
        return false;
    }
    try {
        JWTVerifier verifier = JWT.require(algorithm)
            .withIssuer("auth-api")
            .build();
        verifier.verify(token);
        return true;
    } catch (JwtVerificationException exception) {
        return false;
    }
}
    
    public Date getExpirationFromToken(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        
        try {
            //Decode para blacklist
            DecodedJWT decodedJWT = JWT.decode(token);
            Date expiration = decodedJWT.getExpiresAt();
            
            //Token sem data de expiração - assume 24 horas
            if (expiration == null) {
                return new Date(System.currentTimeMillis() + (24 * 60 * 60 * 1000));
            }
            
            //Se já expirou, mantém a data original mesmo assim
            return expiration;
            
        } catch (JWTDecodeException e) {
            // Token inválido - fallback seguro (1 hora)
            return new Date(System.currentTimeMillis() + (60 * 60 * 1000));
        }
    }
}
