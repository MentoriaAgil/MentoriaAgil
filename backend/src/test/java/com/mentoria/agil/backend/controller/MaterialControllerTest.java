package com.mentoria.agil.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentoria.agil.backend.dto.MaterialRequestDTO;
import com.mentoria.agil.backend.interfaces.service.MaterialServiceInterface;
import com.mentoria.agil.backend.interfaces.service.TokenServiceInterface;
import com.mentoria.agil.backend.model.Material;
import com.mentoria.agil.backend.model.Role;
import com.mentoria.agil.backend.model.TipoMaterial;
import com.mentoria.agil.backend.model.User;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MaterialController.class)
@AutoConfigureMockMvc(addFilters = false)
class MaterialControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private MaterialServiceInterface materialService;
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
        user.setRole(Role.MENTOR);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, null));
    }

    @Test
    void deveCriarMaterial() throws Exception {
        MaterialRequestDTO dto = new MaterialRequestDTO();
        dto.setTitulo("Aula 1");
        dto.setDescricao("Descrição");
        dto.setConteudo("http://link.com");
        dto.setTipo(TipoMaterial.LINK);

        Material material = new Material();
        material.setMentor(new User());
        material.setTipo(TipoMaterial.LINK);

        when(materialService.criarMaterial(any(), any())).thenReturn(material);

        mockMvc.perform(post("/api/materiais")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void deveListarMateriaisDoMentorado() throws Exception {
        when(materialService.listarMateriaisPorMentorado(any())).thenReturn(List.of());
        mockMvc.perform(get("/api/materiais/meus-materiais")).andExpect(status().isOk());
    }
}