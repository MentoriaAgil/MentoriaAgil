package com.mentoria.agil.backend.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.mentoria.agil.backend.model.Role;
import com.mentoria.agil.backend.model.User;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private User usuarioTeste;
    
    private final String SECRET_KEY = "uma-chave-secreta-bem-grande-para-testes-do-jwt-service";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "secretKey", SECRET_KEY);

        usuarioTeste = new User();
        usuarioTeste.setId(1L);
        usuarioTeste.setEmail("hagamenon@email.com");
        usuarioTeste.setRole(Role.MENTOR);
    }

    @Test
    @DisplayName("Deve gerar um token JWT com sucesso para um usuário válido")
    void gerarTokenSucesso() {
        String token = jwtService.generateToken(usuarioTeste);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    @DisplayName("Deve extrair o assunto (email) correto de um token válido")
    void getSubjectFromTokenSucesso() {
        String token = jwtService.generateToken(usuarioTeste);
        
        String subject = jwtService.getSubjectFromToken(token);

        assertNotNull(subject);
        assertEquals(usuarioTeste.getEmail(), subject);
    }

    @Test
    @DisplayName("Deve retornar null ao tentar extrair o assunto de um token com formato ou assinatura inválida")
    void getSubjectFromTokenInvalido() {
        String tokenInvalido = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.invalido.assinatura";
        
        String subject = jwtService.getSubjectFromToken(tokenInvalido);

        assertNull(subject, "Deve retornar null caso ocorra uma JwtException");
    }

    @Test
    @DisplayName("Deve extrair a data de expiração corretamente de um token válido")
    void getExpirationFromTokenSucesso() {
        String token = jwtService.generateToken(usuarioTeste);
        
        Date expiration = jwtService.getExpirationFromToken(token);

        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()), "A data de expiração gerada deve estar no futuro");
    }

    @Test
    @DisplayName("Deve retornar a data inicial (Date 0) ao tentar extrair a expiração de um token inválido")
    void getExpirationFromTokenInvalido() {
        String tokenInvalido = "token.totalmente.invalido";
        
        Date expiration = jwtService.getExpirationFromToken(tokenInvalido);

        assertNotNull(expiration);
        assertEquals(new Date(0), expiration, "Deve retornar Date(0) caso ocorra uma JwtException");
    }
}