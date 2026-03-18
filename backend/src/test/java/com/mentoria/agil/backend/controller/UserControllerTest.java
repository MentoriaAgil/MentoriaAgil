package com.mentoria.agil.backend.controller;

import com.mentoria.agil.backend.dto.response.MentorResponseDTO;
import com.mentoria.agil.backend.interfaces.service.UserServiceInterface;
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

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest { // Removido public

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserServiceInterface userService;
    @MockitoBean
    private UserRepository userRepository;
    @MockitoBean
    private TokenServiceInterface tokenService;
    @MockitoBean
    private TokenBlacklistService tokenBlacklistService;

    @Test
    @DisplayName("Deve listar mentores ordenados")
    void listarMentoresOrdenados() throws Exception {
        MentorResponseDTO m1 = new MentorResponseDTO("Zico", "Esp", "Exp", "Area", "Tipo", "Disp");
        MentorResponseDTO m2 = new MentorResponseDTO("Abel", "Esp", "Exp", "Area", "Tipo", "Disp");

        when(userService.listarMentores(any(), any(), any())).thenReturn(new ArrayList<>(List.of(m1, m2)));

        mockMvc.perform(get("/api/users/mentores").param("ordem", "alfabetica"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Abel"));
    }
}