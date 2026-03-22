package com.mentoria.agil.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentoria.agil.backend.dto.PerfilMentorRequestDTO;
import com.mentoria.agil.backend.interfaces.service.*;
import com.mentoria.agil.backend.model.PerfilMentor;
import com.mentoria.agil.backend.model.Role;
import com.mentoria.agil.backend.model.User;
import com.mentoria.agil.backend.repository.UserRepository;
import com.mentoria.agil.backend.service.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PerfilMentorController.class)
@AutoConfigureMockMvc(addFilters = false)
class PerfilMentorControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private PerfilMentorServiceInterface perfilService;
    @MockitoBean
    private UserRepository userRepository;
    @MockitoBean
    private TokenServiceInterface tokenService;
    @MockitoBean
    private TokenBlacklistService tokenBlacklistService;

    private User mentorMock;
    private PerfilMentor perfilMock;

    @BeforeEach
    void setUp() {
        mentorMock = new User();
        mentorMock.setId(1L);
        mentorMock.setEmail("mentor@teste.com");
        mentorMock.setName("Mentor Teste");
        mentorMock.setRole(Role.MENTOR); // Garante que o principal tem o role

        perfilMock = new PerfilMentor("Java", "5 anos", mentorMock);
        perfilMock.setId(10L);
        perfilMock.setFormacao("Engenharia");

        // Configura o contexto de segurança padrão com autoridades
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(mentorMock, null,
                        List.of(new SimpleGrantedAuthority("ROLE_MENTOR"))));
    }

    @Test
    @DisplayName("Deve criar perfil de mentor com sucesso")
    void deveCriarPerfil() throws Exception {
        PerfilMentorRequestDTO dto = new PerfilMentorRequestDTO("Java", "5 anos", "Eng");
        
        when(userRepository.findByEmail(anyString())).thenReturn(java.util.Optional.of(mentorMock));
        when(perfilService.criarPerfilMentor(any(), anyString(), anyString(), anyString())).thenReturn(perfilMock);

        mockMvc.perform(post("/api/mentors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Deve buscar mentor por ID")
    void deveBuscarPorId() throws Exception {
        when(perfilService.buscarPorId(10L)).thenReturn(perfilMock);
        mockMvc.perform(get("/api/mentors/10")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve listar todos os perfis")
    void deveListarTodos() throws Exception {
        when(perfilService.listarTodos()).thenReturn(List.of(perfilMock));
        mockMvc.perform(get("/api/mentors")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve retornar 403 ao tentar atualizar perfil de outro usuário")
    void deveRetornar403AtualizarOutroUsuario() throws Exception {
        User outroUsuario = new User();
        outroUsuario.setEmail("outro@teste.com");
        perfilMock.setUser(outroUsuario);

        when(perfilService.buscarPorId(10L)).thenReturn(perfilMock);

        PerfilMentorRequestDTO dto = new PerfilMentorRequestDTO("J", "5", "E");

        mockMvc.perform(put("/api/mentors/10")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve retornar 403 ao tentar deletar perfil de outro usuário sem ser admin")
    void deveRetornar403DeletarOutroUsuario() throws Exception {
        User outroUsuario = new User();
        outroUsuario.setEmail("outro@teste.com");
        perfilMock.setUser(outroUsuario);

        when(perfilService.buscarPorId(10L)).thenReturn(perfilMock);

        mockMvc.perform(delete("/api/mentors/10"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve permitir que administrador delete qualquer perfil")
    void devePermitirAdminDeletar() throws Exception {
        User admin = new User();
        admin.setEmail("admin@teste.com");
        admin.setRole(Role.ADMIN);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(admin, null,
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));

        when(perfilService.buscarPorId(10L)).thenReturn(perfilMock);

        mockMvc.perform(delete("/api/mentors/10"))
                .andExpect(status().isNoContent());

        verify(perfilService).deletar(10L);
    }

    @Test
    @DisplayName("Deve atualizar perfil com sucesso")
    void deveAtualizarMentor() throws Exception {
        when(perfilService.buscarPorId(10L)).thenReturn(perfilMock);
        when(perfilService.atualizar(any(), any())).thenReturn(perfilMock);

        PerfilMentorRequestDTO dto = new PerfilMentorRequestDTO("Java",
                "5 anos", "Eng");

        mockMvc.perform(put("/api/mentors/10")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }
}