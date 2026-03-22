package com.mentoria.agil.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentoria.agil.backend.dto.MentoriaRequestDTO;
import com.mentoria.agil.backend.dto.MentoriaRequestUpdateDTO;
import com.mentoria.agil.backend.interfaces.service.*;
import com.mentoria.agil.backend.model.*;
import com.mentoria.agil.backend.repository.UserRepository;
import com.mentoria.agil.backend.service.TokenBlacklistService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MentoriaRequestController.class)
@AutoConfigureMockMvc(addFilters = false)
class MentoriaRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private MentoriaRequestServiceInterface requestService;
    @MockitoBean
    private UserRepository userRepository;
    @MockitoBean
    private TokenServiceInterface tokenService;
    @MockitoBean
    private TokenBlacklistService tokenBlacklistService;

    @Test
    void deveRetornar400QuandoCriarSolicitacaoComDadosInvalidos() throws Exception {
        MentoriaRequestDTO dtoInvalido = new MentoriaRequestDTO();
        dtoInvalido.setMentorId(null);
        dtoInvalido.setMessage("");

        mockMvc.perform(post("/api/mentorias/request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar404QuandoMentorNaoEncontradoAoListarPendentes() throws Exception {
        // Simula o contexto de segurança com um UserDetails
        org.springframework.security.core.userdetails.User userDetails = 
            new org.springframework.security.core.userdetails.User("erro@email.com", "senha", List.of());
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, null));

        // Simula a falha na busca do usuário no banco
        when(userRepository.findByEmail("erro@email.com")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/mentorias/pendentes"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveListarSolicitacoesPendentesComSucesso() throws Exception {
        User mentor = new User("Mentor", "mentor@email.com", "senha");
        org.springframework.security.core.userdetails.User userDetails = 
            new org.springframework.security.core.userdetails.User("mentor@email.com", "senha", List.of());
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, null));

        when(userRepository.findByEmail("mentor@email.com")).thenReturn(Optional.of(mentor));
        when(requestService.listarPendentes(mentor)).thenReturn(List.of());

        mockMvc.perform(get("/api/mentorias/pendentes"))
                .andExpect(status().isOk());
    }

    @Test
    void deveCriarSolicitacao() throws Exception {
        User mentorado = new User();
        mentorado.setId(1L);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(mentorado, null));

        MentoriaRequestDTO dto = new MentoriaRequestDTO();
        dto.setMentorId(2L);
        dto.setMessage("Quero mentoria");

        MentoriaRequest request = new MentoriaRequest();
        request.setMentor(new User());
        request.setMentorado(mentorado);
        request.setStatus(MentoriaStatus.PENDING);

        when(requestService.createRequest(any(), any())).thenReturn(request);

        mockMvc.perform(post("/api/mentorias/request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void deveListarSolicitacoesPendentes() throws Exception {
        User mentor = new User();
        mentor.setEmail("mentor@teste.com");
        
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(mentor, null));

        when(userRepository.findByEmail(any())).thenReturn(java.util.Optional.of(mentor));
        when(requestService.listarPendentes(any())).thenReturn(List.of());

        mockMvc.perform(get("/api/mentorias/pendentes")).andExpect(status().isOk());
    }

    @Test
    void deveAtualizarStatusDaSolicitacao() throws Exception {
        User mentor = new User();
        mentor.setEmail("mentor@teste.com");
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(mentor, null));

        MentoriaRequestUpdateDTO dto = new MentoriaRequestUpdateDTO();
        dto.setStatus(MentoriaStatus.ACCEPTED);

        MentoriaRequest request = new MentoriaRequest();
        request.setMentor(mentor);
        request.setMentorado(new User());

        when(userRepository.findByEmail(any())).thenReturn(java.util.Optional.of(mentor));
        when(requestService.atualizarStatus(any(), any(), any())).thenReturn(request);

        mockMvc.perform(patch("/api/mentorias/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }
}