package com.mentoria.agil.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mentoria.agil.backend.dto.AgendamentoRequestDTO;
import com.mentoria.agil.backend.interfaces.service.AgendamentoServiceInterface;
import com.mentoria.agil.backend.interfaces.service.TokenServiceInterface;
import com.mentoria.agil.backend.model.*;
import com.mentoria.agil.backend.repository.UserRepository;
import com.mentoria.agil.backend.service.TokenBlacklistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AgendamentoController.class)
@AutoConfigureMockMvc(addFilters = false)
class AgendamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @MockitoBean
    private AgendamentoServiceInterface agendamentoService;
    @MockitoBean
    private UserRepository userRepository;
    @MockitoBean
    private TokenServiceInterface tokenService;
    @MockitoBean
    private TokenBlacklistService tokenBlacklistService;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setId(1L);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, null));
    }

    @Test
    void deveAgendarSessao() throws Exception {
        AgendamentoRequestDTO dto = new AgendamentoRequestDTO();
        dto.setMentorId(2L);
        dto.setDataHoraInicio(LocalDateTime.now().plusDays(2));
        dto.setDataHoraFim(LocalDateTime.now().plusDays(2).plusHours(1));
        dto.setFormato(FormatoSessao.ONLINE);
        dto.setLinkReuniao("http://meet.com");

        Sessao sessao = new Sessao();
        sessao.setMentor(new User());
        sessao.setMentorado(new User());
        sessao.setFormato(FormatoSessao.ONLINE);

        when(agendamentoService.agendar(any(), any())).thenReturn(sessao);

        mockMvc.perform(post("/api/sessoes/agendar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }
}