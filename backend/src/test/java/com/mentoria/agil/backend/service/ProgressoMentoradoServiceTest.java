package com.mentoria.agil.backend.service;

import com.mentoria.agil.backend.dto.ProgressoRequestDTO;
import com.mentoria.agil.backend.dto.response.ProgressoResponseDTO;
import com.mentoria.agil.backend.exception.BusinessException;
import com.mentoria.agil.backend.model.ProgressoMentorado;
import com.mentoria.agil.backend.model.User;
import com.mentoria.agil.backend.repository.ProgressoMentoradoRepository;
import com.mentoria.agil.backend.repository.SessaoRepository;
import com.mentoria.agil.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProgressoMentoradoServiceTest {

    @Mock
    private ProgressoMentoradoRepository repository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SessaoRepository sessaoRepository;

    @InjectMocks
    private ProgressoMentoradoService service;

    private User mentor;
    private User mentorado;
    private ProgressoRequestDTO dto;

    @BeforeEach
    void setUp() {
        mentor = new User();
        mentor.setId(1L);
        mentor.setName("Mentor Teste");

        mentorado = new User();
        mentorado.setId(2L);
        mentorado.setName("Mentorado Teste");

        dto = new ProgressoRequestDTO(2L, "Evolução constante");
    }

    @Test
    @DisplayName("Deve registrar progresso com sucesso")
    void deveRegistrarComSucesso() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(mentorado));
        when(sessaoRepository.existsByMentorAndMentorado(mentor, mentorado)).thenReturn(true);

        ProgressoMentorado mockSalvo = new ProgressoMentorado();
        mockSalvo.setId(10L);
        mockSalvo.setMentor(mentor);
        mockSalvo.setMentorado(mentorado);
        mockSalvo.setDescricao("Evolução constante");
        mockSalvo.setDataRegistro(LocalDateTime.now());

        when(repository.save(any())).thenReturn(mockSalvo);

        ProgressoResponseDTO result = service.registrar(mentor, dto);

        assertNotNull(result);
        assertEquals("Evolução constante", result.descricao());
        verify(repository).save(any());
    }

    @Test
    @DisplayName("Deve excluir registro com sucesso")
    void deveExcluirComSucesso() {
        ProgressoMentorado p = new ProgressoMentorado();
        p.setMentor(mentor);
        when(repository.findById(10L)).thenReturn(Optional.of(p));

        assertDoesNotThrow(() -> service.excluir(10L, mentor));
        verify(repository).delete(p);
    }

    @Test
    @DisplayName("Deve falhar ao registrar progresso para aluno sem sessoes previas")
    void deveFalharSemVinculo() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(mentorado));
        when(sessaoRepository.existsByMentorAndMentorado(mentor, mentorado)).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class, () -> service.registrar(mentor, dto));
        assertTrue(ex.getMessage().toLowerCase().contains("historico de sessoes"));
    }

    @Test
    @DisplayName("Deve listar progressos sem erro de NullPointer")
    void deveListarPorMentorado() {
        ProgressoMentorado p = new ProgressoMentorado();
        p.setMentor(mentor);
        p.setMentorado(mentorado);
        p.setDescricao("Teste");

        when(userRepository.findById(2L)).thenReturn(Optional.of(mentorado));
        when(repository.findByMentoradoOrderByDataRegistroDesc(mentorado)).thenReturn(List.of(p));

        List<ProgressoResponseDTO> list = service.listarPorMentorado(2L);
        assertFalse(list.isEmpty());
    }
}