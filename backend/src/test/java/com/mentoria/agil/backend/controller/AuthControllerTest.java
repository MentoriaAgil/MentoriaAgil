package com.mentoria.agil.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentoria.agil.backend.dto.LoginDTO;
import com.mentoria.agil.backend.dto.UserRequestDTO;
import com.mentoria.agil.backend.dto.response.LoginResponseDTO;
import com.mentoria.agil.backend.interfaces.facade.AuthFacadeInterface;
import com.mentoria.agil.backend.interfaces.service.TokenServiceInterface;
import com.mentoria.agil.backend.model.Role;
import com.mentoria.agil.backend.repository.UserRepository;
import com.mentoria.agil.backend.service.TokenBlacklistService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean private AuthFacadeInterface authFacade;
    @MockitoBean private UserRepository userRepository;
    @MockitoBean private TokenServiceInterface tokenService;
    @MockitoBean private TokenBlacklistService tokenBlacklistService;

    @Test
    void deveFazerLogin() throws Exception {
        LoginDTO dto = new LoginDTO("user@email.com", "senha123");
        when(authFacade.autenticar(any())).thenReturn(new LoginResponseDTO("token", null, null, null));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void deveRegistrarUsuario() throws Exception {
        UserRequestDTO dto = new UserRequestDTO("Nome", "e@e.com", "senha12345", Role.ESTUDANTE);
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void deveFazerLogout() throws Exception {
        mockMvc.perform(post("/api/auth/logout")
                .header("Authorization", "Bearer token"))
                .andExpect(status().isNoContent());
    }
}