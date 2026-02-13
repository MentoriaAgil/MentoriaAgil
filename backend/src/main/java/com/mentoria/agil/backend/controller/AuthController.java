package com.mentoria.agil.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mentoria.agil.backend.dto.LoginDTO;
import com.mentoria.agil.backend.dto.UserDTO;
import com.mentoria.agil.backend.model.User;
import com.mentoria.agil.backend.service.JwtService;
import com.mentoria.agil.backend.service.TokenBlacklistService;
import com.mentoria.agil.backend.service.UserService;

//classe de controle de autenticação e autorização
@RestController
@RequestMapping("/auth")
public class AuthController {
    
    private final TokenBlacklistService tokenBlacklistService;
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService, JwtService jwtService, PasswordEncoder passwordEncoder, TokenBlacklistService tokenBlacklistService) {
      this.userService = userService;
      this.jwtService = jwtService;
      this.passwordEncoder = passwordEncoder;
      this.tokenBlacklistService = tokenBlacklistService;
  }
  
    @PostMapping("/register")
    public ResponseEntity<String> registrarUsuario(@RequestBody UserDTO userDTO) {
        try {
            User novoUsuario = userService.salvarUsuario(userDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body("Usuário cadastrado com sucesso. ID: " + novoUsuario.getId());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao cadastrar usuário: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDTO dto) {
        // 1. Busca o usuário por email
        var user = userService.buscarPorEmail(dto.email());
    
        // 2. Verifica se a senha está correta (usando o encoder)
        if (user != null && passwordEncoder.matches(dto.password(), user.getPassword())) {
            // 3. Gera e retorna o token
            String token = jwtService.generateToken(user);
            return ResponseEntity.ok(token);
    }
    
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("E-mail ou senha inválidos");
}
  
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            tokenBlacklistService.invalidateToken(token);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}