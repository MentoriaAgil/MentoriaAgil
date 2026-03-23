package com.mentoria.agil.backend.config;

import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.mentoria.agil.backend.interfaces.service.TokenServiceInterface;
import com.mentoria.agil.backend.repository.UserRepository;
import com.mentoria.agil.backend.service.TokenBlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenServiceInterface tokenServiceInterface;
    private final UserRepository userRepository;
    private final TokenBlacklistService blacklistService;

    public SecurityFilter(TokenServiceInterface tokenServiceInterface, UserRepository userRepository,
            TokenBlacklistService blacklistService) {
        this.tokenServiceInterface = tokenServiceInterface;
        this.userRepository = userRepository;
        this.blacklistService = blacklistService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = recoverToken(request);

        if (token != null && !token.isEmpty()) {
            if (blacklistService.isTokenBlacklisted(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            String email = tokenServiceInterface.getSubjectFromToken(token);

            if (email != null) {
                UserDetails user = userRepository.findByEmail(email).orElse(null);

                if (user != null) {
                    var auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.replace("Bearer ", "").trim();
    }
}