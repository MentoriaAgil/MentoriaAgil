package com.mentoria.agil.backend.service;

import org.springframework.stereotype.Service;
import com.mentoria.agil.backend.service.JwtService;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {
    
    private final ConcurrentHashMap<String, Long> blacklistedTokens = new ConcurrentHashMap<>();
    
    private final JwtService tokenService;
    
    public TokenBlacklistService(JwtService tokenService) {
        this.tokenService = tokenService;
    }
    
    public void invalidateToken(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token não pode ser vazio");
        }

        //Remove "Bearer " se existir
        String cleanToken = cleanToken(token);
        
        //Extrai expiração
        Date expiration = tokenService.getExpirationFromToken(cleanToken);
        
        //Armazena sempre sem o prefixo
        blacklistedTokens.put(cleanToken, expiration.getTime());
        cleanExpiredTokens();
    }
    
    public boolean isTokenBlacklisted(String token) {
        cleanExpiredTokens();
        
        //Sempre limpa o token antes de verificar
        String cleanToken = cleanToken(token);
        return blacklistedTokens.containsKey(cleanToken);
    }

    private String cleanToken(String token) {
        if (token == null) return null;
        
        //Remove "Bearer " se presente (case insensitive)
        String cleaned = token.replaceFirst("^(?i)Bearer\\s+", "");
        
        //Remove espaços extras
        return cleaned.trim();
    }
    
    private void cleanExpiredTokens() {
        long now = System.currentTimeMillis();  
        blacklistedTokens.entrySet().removeIf(entry -> entry.getValue() < now);
    }
    
    public int getBlacklistSize() {
        cleanExpiredTokens();
        return blacklistedTokens.size();
    }
}