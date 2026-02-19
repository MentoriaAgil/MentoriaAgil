package com.mentoria.agil.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Date;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TokenBlacklistServiceTest {

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private TokenBlacklistService tokenBlacklistService;

    private final String rawToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJoYWdhbWVub25AZW1haWwuY29tIn0";
    private final String bearerToken = "Bearer " + rawToken;

    @Test
    @DisplayName("Deve invalidar o token com sucesso, removendo o prefixo Bearer")
    void invalidateTokenSuccess() {
        long futureTime = System.currentTimeMillis() + 3600000;
        Date expirationDate = new Date(futureTime);

        when(jwtService.getExpirationFromToken(rawToken)).thenReturn(expirationDate);

        tokenBlacklistService.invalidateToken(bearerToken);

        assertTrue(tokenBlacklistService.isTokenBlacklisted(rawToken), "O token limpo (sem Bearer) deve estar na blacklist");
        verify(jwtService, times(1)).getExpirationFromToken(rawToken);
    }

    @Test
    @DisplayName("Deve retornar false para um token que não foi invalidado")
    void isTokenBlacklistedFalse() {
        boolean result = tokenBlacklistService.isTokenBlacklisted("token-aleatorio-valido");

        assertFalse(result, "Deve retornar false para tokens que não estão na blacklist");
    }

    @Test
    @DisplayName("Deve limpar tokens expirados do mapa ao tentar invalidar um novo token")
    void cleanExpiredTokensSuccess() {
        long pastTime = System.currentTimeMillis() - 3600000; 
        Date pastExpiration = new Date(pastTime);

        when(jwtService.getExpirationFromToken(rawToken)).thenReturn(pastExpiration);

        tokenBlacklistService.invalidateToken(bearerToken);

        assertFalse(tokenBlacklistService.isTokenBlacklisted(rawToken), "O token recém-adicionado mas já expirado deve ser limpo imediatamente");
    }
    
    @Test
    @DisplayName("Deve checar corretamente se o token está na blacklist mesmo com espaços em branco")
    void isTokenBlacklistedWithSpaces() {
        long futureTime = System.currentTimeMillis() + 3600000;
        Date expirationDate = new Date(futureTime);
        when(jwtService.getExpirationFromToken(rawToken)).thenReturn(expirationDate);
        
        tokenBlacklistService.invalidateToken(bearerToken);

        assertTrue(tokenBlacklistService.isTokenBlacklisted("   " + rawToken + "   "), "O método isTokenBlacklisted deve usar o trim() no token");
    }
}