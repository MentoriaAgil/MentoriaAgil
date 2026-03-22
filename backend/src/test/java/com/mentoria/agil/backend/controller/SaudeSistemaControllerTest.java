package com.mentoria.agil.backend.controller;

import com.mentoria.agil.backend.interfaces.service.TokenServiceInterface;
import com.mentoria.agil.backend.repository.UserRepository;
import com.mentoria.agil.backend.service.TokenBlacklistService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SaudeSistemaController.class)
@AutoConfigureMockMvc(addFilters = false)
public class SaudeSistemaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepository;
    @MockitoBean
    private TokenServiceInterface tokenService;
    @MockitoBean
    private TokenBlacklistService tokenBlacklistService;

    @Test
    @DisplayName("Deve retornar status 200 e JSON indicando que o sistema está UP")
    void deveRetornarStatusOk() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }
}