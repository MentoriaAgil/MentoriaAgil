package com.mentoria.agil.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.mentoria.agil.backend.dto.LoginDTO;
import com.mentoria.agil.backend.model.User;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthenticationService authenticationService;

    private User userMock;
    private LoginDTO loginDTO;
    private Validator validator;

    @BeforeEach
    void setUp() {
    	ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        
        userMock = new User();
        userMock.setId(1L);
        userMock.setEmail("usuario@email.com");
        userMock.setPassword("senhaCodificadaBcrypt");

        loginDTO = new LoginDTO("usuario@email.com", "senha123");
    }

    @Test
    @DisplayName("Deve realizar login com sucesso e retornar um token JWT")
    void loginSucesso() {
        when(userService.buscarPorEmail(loginDTO.email())).thenReturn(userMock);
        when(passwordEncoder.matches(loginDTO.password(), userMock.getPassword())).thenReturn(true);
        when(jwtService.generateToken(userMock)).thenReturn("eyJh...mocked_token");

        String tokenGerado = authenticationService.login(loginDTO);

        assertNotNull(tokenGerado, "O token retornado não deveria ser nulo");
        assertEquals("eyJh...mocked_token", tokenGerado);
        
        verify(userService, times(1)).buscarPorEmail(loginDTO.email());
        verify(passwordEncoder, times(1)).matches(loginDTO.password(), userMock.getPassword());
        verify(jwtService, times(1)).generateToken(userMock);
    }

    @Test
    @DisplayName("Deve lançar BadCredentialsException quando o e-mail não for encontrado")
    void loginEmailInvalido() {
        when(userService.buscarPorEmail(loginDTO.email())).thenReturn(null);

        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
            authenticationService.login(loginDTO);
        });

        assertEquals("E-mail ou senha inválidos", exception.getMessage());

        verify(userService, times(1)).buscarPorEmail(loginDTO.email());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar BadCredentialsException quando a senha estiver incorreta")
    void loginSenhaIncorreta() {
        when(userService.buscarPorEmail(loginDTO.email())).thenReturn(userMock);
        when(passwordEncoder.matches(loginDTO.password(), userMock.getPassword())).thenReturn(false);

        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
            authenticationService.login(loginDTO);
        });

        assertEquals("E-mail ou senha inválidos", exception.getMessage());

        verify(userService, times(1)).buscarPorEmail(loginDTO.email());
        verify(passwordEncoder, times(1)).matches(loginDTO.password(), userMock.getPassword());
        verify(jwtService, never()).generateToken(any(User.class));
    }
    
    @Test
    @DisplayName("Deve falhar na validação se o e-mail estiver em branco ou nulo")
    void validacaoEmailEmBranco() {
        LoginDTO dtoSemEmail = new LoginDTO("", "senha123");
        Set<ConstraintViolation<LoginDTO>> violacoes = validator.validate(dtoSemEmail);
        
        assertFalse(violacoes.isEmpty(), "Deveria haver erro de validação para e-mail vazio");
    }

    @Test
    @DisplayName("Deve falhar na validação se a senha estiver em branco ou nula")
    void validacaoSenhaEmBranco() {
        LoginDTO dtoSemSenha = new LoginDTO("usuario@email.com", "   ");
        Set<ConstraintViolation<LoginDTO>> violacoes = validator.validate(dtoSemSenha);
        
        assertFalse(violacoes.isEmpty(), "Deveria haver erro de validação para senha vazia");
    }
}