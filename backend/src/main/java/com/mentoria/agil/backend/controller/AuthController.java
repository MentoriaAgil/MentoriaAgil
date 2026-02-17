package com.mentoria.agil.backend.controller;

import com.mentoria.agil.backend.dto.LoginDTO;
import com.mentoria.agil.backend.dto.response.LoginResponseDTO;
import com.mentoria.agil.backend.interfaces.facade.AuthFacadeInterface;

import com.mentoria.agil.backend.dto.UserRequestDTO;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    private final AuthFacadeInterface authFacade;

    public AuthController(AuthFacadeInterface authFacade) {
        this.authFacade = authFacade;
    }
  
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid UserRequestDTO dto) {
        authFacade.registrarNovoUsuario(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginDTO dto) {
        return ResponseEntity.ok(authFacade.autenticar(dto));
    }
  
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authHeader) {
        authFacade.encerrarSessao(authHeader);
        return ResponseEntity.noContent().build();
    }
}