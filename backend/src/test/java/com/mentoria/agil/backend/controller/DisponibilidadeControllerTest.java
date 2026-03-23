package com.mentoria.agil.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mentoria.agil.backend.dto.DisponibilidadeRequestDTO;
import com.mentoria.agil.backend.enums.Role;
import com.mentoria.agil.backend.interfaces.service.DisponibilidadeServiceInterface;
import com.mentoria.agil.backend.interfaces.service.TokenServiceInterface;
import com.mentoria.agil.backend.model.Disponibilidade;
import com.mentoria.agil.backend.model.User;
import com.mentoria.agil.backend.repository.UserRepository;
import com.mentoria.agil.backend.service.TokenBlacklistService;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DisponibilidadeController.class)
@AutoConfigureMockMvc(addFilters = false)
class DisponibilidadeControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @MockitoBean
    private DisponibilidadeServiceInterface disponibilidadeService;
    @MockitoBean
    private UserRepository userRepository;
    @MockitoBean
    private TokenServiceInterface tokenService;
    @MockitoBean
    private TokenBlacklistService tokenBlacklistService;

    @BeforeEach
    void setUp() {
        User user = new User();
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, null));
    }

    @Test
    void deveCadastrarDisponibilidade() throws Exception {
        DisponibilidadeRequestDTO dto = new DisponibilidadeRequestDTO();
        dto.setDataHoraInicio(LocalDateTime.now().plusDays(1));
        dto.setDataHoraFim(LocalDateTime.now().plusDays(1).plusHours(1));

        Disponibilidade disp = new Disponibilidade(new User(), dto.getDataHoraInicio(), dto.getDataHoraFim());
        when(disponibilidadeService.cadastrar(any(), any())).thenReturn(disp);

        mockMvc.perform(post("/api/disponibilidades")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Deve listar disponibilidades do mentor logado com sucesso")
    void deveListarDisponibilidadesComSucesso() throws Exception {
        // Simula o usuário mentor logado
        User mentorLogado = new User();
        mentorLogado.setId(1L);
        mentorLogado.setEmail("mentor@teste.com");
        mentorLogado.setRole(Role.MENTOR);

        mockMvc.perform(get("/api/disponibilidades/minhas")
                .with(user(mentorLogado)) // Simulação do Spring Security
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve retornar 404 quando listar disponibilidades de mentor inexistente")
    void deveRetornar404ListarMentorInexistente() throws Exception {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Agora a rota pública existe novamente
        mockMvc.perform(get("/api/disponibilidades/mentor/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve listar disponibilidades do mentor logado com sucesso")
    void deveListarMinhasDisponibilidadesComSucesso() throws Exception {
        User mentor = new User();
        mentor.setId(1L);
        // Simula o @AuthenticationPrincipal
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(mentor, null));

        when(disponibilidadeService.listarDisponibilidadesFuturas(any())).thenReturn(List.of());

        mockMvc.perform(get("/api/disponibilidades/minhas"))
                .andExpect(status().isOk());
    }
}