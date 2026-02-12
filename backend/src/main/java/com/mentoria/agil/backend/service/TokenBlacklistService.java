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
        //Extrair data de expiração do token
        Date expirationDate = tokenService.getExpirationFromToken(token);
        
        if (expirationDate != null) {
            blacklistedTokens.put(token, expirationDate.getTime());
        } else {
            // expira em 24h se não conseguir extrair
            long fallbackExpiration = System.currentTimeMillis() + (24 * 60 * 60 * 1000);
            blacklistedTokens.put(token, fallbackExpiration);
        }
        
        //Limpa tokens expirados
        cleanExpiredTokens();
    }
    
    public boolean isTokenBlacklisted(String token) {
        //Limpa expirados antes de verificar
        cleanExpiredTokens();
        return blacklistedTokens.containsKey(token);
    }
    
    private void cleanExpiredTokens() {
        long now = System.currentTimeMillis();
        
        blacklistedTokens.entrySet().removeIf(entry -> {
            Long expirationTime = entry.getValue();
            return expirationTime < now; // Token expirado pode ser removido
        });
    }
    
    public int getBlacklistSize() {
        cleanExpiredTokens();
        return blacklistedTokens.size();
    }
}