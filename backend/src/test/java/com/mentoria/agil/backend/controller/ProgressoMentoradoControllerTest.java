package com.mentoria.agil.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mentoria.agil.backend.dto.ProgressoRequestDTO;
import com.mentoria.agil.backend.interfaces.service.TokenServiceInterface;
import com.mentoria.agil.backend.repository.UserRepository;
import com.mentoria.agil.backend.service.ProgressoMentoradoService;
import com.mentoria.agil.backend.service.TokenBlacklistService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProgressoMentoradoController.class)
class ProgressoMentoradoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @MockitoBean
    private ProgressoMentoradoService service;
    @MockitoBean
    private UserRepository userRepository;
    @MockitoBean
    private TokenServiceInterface tokenService;
    @MockitoBean
    private TokenBlacklistService tokenBlacklistService;

    @Test
    @WithMockUser(authorities = "ESTUDANTE")
    @DisplayName("Deve retornar 403 quando um estudante tentar registrar progresso")
    void deveRetornarForbiddenParaNaoMentoresAoRegistrar() throws Exception {
        ProgressoRequestDTO dto = new ProgressoRequestDTO(2L, "Teste");

        mockMvc.perform(post("/api/progresso")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "MENTOR")
    void deveRegistrarProgresso() throws Exception {
        ProgressoRequestDTO dto = new ProgressoRequestDTO(2L, "Teste");

        mockMvc.perform(post("/api/progresso")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void deveListarProgresso() throws Exception {
        when(service.listarPorMentorado(any())).thenReturn(List.of());

        mockMvc.perform(get("/api/progresso/mentorado/2"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "MENTOR")
    void deveExcluirProgresso() throws Exception {
        mockMvc.perform(delete("/api/progresso/10")
                .with(csrf()))
                .andExpect(status().isNoContent());
    }
}