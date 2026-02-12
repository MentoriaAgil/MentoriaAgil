package com.mentoria.agil.backend.service;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import com.mentoria.agil.backend.model.User;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import com.okta.jwt.JwtVerificationException;

import com.mentoria.agil.backend.service.TokenBlacklistService;

@Service
public class JwtService {

    // Em produção, usar uma chave longa e secreta vinda de variáveis de ambiente
    private final String SECRET_KEY = "sua_chave_secreta_muito_longa_e_segura_para_o_projeto";

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String generateToken(Object user) {
        return Jwts.builder()
                .subject(((User) user).getEmail())
                .claim("role", ((User) user).getRole())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 86400000)) // 24 horas
                .signWith(getSigningKey())
                .compact();
    }
}

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
