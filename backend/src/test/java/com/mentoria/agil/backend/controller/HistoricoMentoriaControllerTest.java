package com.mentoria.agil.backend.controller;

import com.mentoria.agil.backend.interfaces.service.HistoricoMentoriaServiceInterface;
import com.mentoria.agil.backend.interfaces.service.TokenServiceInterface;
import com.mentoria.agil.backend.model.User;
import com.mentoria.agil.backend.repository.UserRepository;
import com.mentoria.agil.backend.service.TokenBlacklistService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HistoricoMentoriaController.class)
@AutoConfigureMockMvc(addFilters = false)
class HistoricoMentoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HistoricoMentoriaServiceInterface historicoService;
    @MockitoBean
    private UserRepository userRepository;
    @MockitoBean
    private TokenServiceInterface tokenService;
    @MockitoBean
    private TokenBlacklistService tokenBlacklistService;

    @Test
    void deveListarHistorico() throws Exception {
        User mentorado = new User();
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(mentorado, null));

        when(historicoService.listarHistorico(any(), any())).thenReturn(List.of());

        mockMvc.perform(get("/api/mentorias/historico")).andExpect(status().isOk());
    }
}