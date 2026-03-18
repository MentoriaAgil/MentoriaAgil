package com.mentoria.agil.backend.controller;

import com.mentoria.agil.backend.interfaces.service.TokenServiceInterface;
import com.mentoria.agil.backend.model.*;
import com.mentoria.agil.backend.repository.*;
import com.mentoria.agil.backend.service.TokenBlacklistService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SessaoMaterialController.class)
@AutoConfigureMockMvc(addFilters = false)
class SessaoMaterialControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private SessaoRepository sessaoRepository;
    @MockitoBean
    private MaterialRepository materialRepository;
    @MockitoBean
    private SessaoMaterialRepository sessaoMaterialRepository;
    @MockitoBean
    private UserRepository userRepository;
    @MockitoBean
    private TokenServiceInterface tokenService;
    @MockitoBean
    private TokenBlacklistService tokenBlacklistService;

    private User mentor;

    @BeforeEach
    void setUp() {
        mentor = new User();
        mentor.setId(1L);
        // Garante que o controlador consiga fazer o cast para User
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(mentor, null));
    }

    @Test
    @DisplayName("Deve falhar quando a sessão não existe")
    void deveFalharSessaoInexistente() throws Exception {
        when(sessaoRepository.findById(1L)).thenReturn(Optional.empty());
        mockMvc.perform(post("/api/sessoes/1/materiais/1"))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof EntityNotFoundException));
    }

    @Test
    @DisplayName("Deve falhar quando o material não existe")
    void deveFalharMaterialInexistente() throws Exception {
        when(sessaoRepository.findById(1L)).thenReturn(Optional.of(new Sessao()));
        when(materialRepository.findById(1L)).thenReturn(Optional.empty());
        mockMvc.perform(post("/api/sessoes/1/materiais/1"))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof EntityNotFoundException));
    }

    @Test
    @DisplayName("Deve falhar quando o usuário não é o mentor da sessão")
    void deveFalharUsuarioNaoMentor() throws Exception {
        User outroMentor = new User();
        outroMentor.setId(99L);
        Sessao sessao = new Sessao();
        sessao.setMentor(outroMentor);

        when(sessaoRepository.findById(1L)).thenReturn(Optional.of(sessao));
        when(materialRepository.findById(1L)).thenReturn(Optional.of(new Material()));

        mockMvc.perform(post("/api/sessoes/1/materiais/1"))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof SecurityException));
    }

    @Test
    @DisplayName("Não deve criar novo vínculo se o material já estiver associado")
    void naoDeveDuplicarVinculo() throws Exception {
        Sessao sessao = new Sessao();
        sessao.setMentor(mentor);
        Material material = new Material();
        material.setId(10L);
        SessaoMaterial vinculo = new SessaoMaterial(sessao, material);

        when(sessaoRepository.findById(1L)).thenReturn(Optional.of(sessao));
        when(materialRepository.findById(10L)).thenReturn(Optional.of(material));
        // Simula que o material já existe na lista retornada pelo stream()
        when(sessaoMaterialRepository.findBySessaoId(1L)).thenReturn(List.of(vinculo));

        mockMvc.perform(post("/api/sessoes/1/materiais/10")).andExpect(status().isOk());
        // Verifica que o save não foi chamado (cobre o ramo 'false' do if !exists)
        verify(sessaoMaterialRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve vincular material com sucesso quando não existe vínculo")
    void deveVincularComSucesso() throws Exception {
        Sessao sessao = new Sessao();
        sessao.setMentor(mentor);
        when(sessaoRepository.findById(1L)).thenReturn(Optional.of(sessao));
        when(materialRepository.findById(1L)).thenReturn(Optional.of(new Material()));
        when(sessaoMaterialRepository.findBySessaoId(1L)).thenReturn(List.of());

        mockMvc.perform(post("/api/sessoes/1/materiais/1")).andExpect(status().isOk());
        verify(sessaoMaterialRepository, times(1)).save(any());
    }
}