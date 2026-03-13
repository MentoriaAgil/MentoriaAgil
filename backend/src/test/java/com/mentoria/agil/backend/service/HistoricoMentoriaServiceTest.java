package com.mentoria.agil.backend.service;

import com.mentoria.agil.backend.dto.response.HistoricoSessaoDTO;
import com.mentoria.agil.backend.model.*;
import com.mentoria.agil.backend.repository.MaterialMentoradoRepository;
import com.mentoria.agil.backend.repository.SessaoRepository;
import com.mentoria.agil.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
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
class HistoricoMentoriaServiceTest {

    @Mock
    private SessaoRepository sessaoRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MaterialMentoradoRepository materialMentoradoRepository;

    @InjectMocks
    private HistoricoMentoriaService historicoService;

    private User mentorado;
    private User mentor;
    private Sessao sessao1, sessao2;
    private Material material1, material2;
    private MaterialMentorado assoc1, assoc2;

    @BeforeEach
    void setUp() {
        mentorado = new User("João", "joao@email.com", "123");
        mentorado.setId(1L);
        mentorado.setRole(Role.ESTUDANTE);

        mentor = new User("Maria", "maria@email.com", "456");
        mentor.setId(2L);
        mentor.setRole(Role.MENTOR);

        sessao1 = new Sessao(mentor, mentorado, 
                LocalDateTime.now().minusDays(5), 
                LocalDateTime.now().minusDays(5).plusHours(1), 
                SessaoStatus.CONCLUIDA, 
                "Observação 1");
        sessao1.setId(10L);

        sessao2 = new Sessao(mentor, mentorado,
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(2).plusHours(1),
                SessaoStatus.CONCLUIDA,
                "Observação 2");
        sessao2.setId(20L);

        material1 = new Material("Material 1", "Desc 1", TipoMaterial.LINK, "http://exemplo.com", mentor);
        material1.setId(100L);
        material2 = new Material("Material 2", "Desc 2", TipoMaterial.DOCUMENTO, "doc.pdf", mentor);
        material2.setId(200L);

        assoc1 = new MaterialMentorado(material1, mentorado);
        assoc1.setId(1000L);
        assoc2 = new MaterialMentorado(material2, mentorado);
        assoc2.setId(2000L);
    }

    @Test
    void deveListarHistoricoSemFiltroDeMentor() {
        when(sessaoRepository.findByMentoradoAndStatusOrderByDataHoraInicioDesc(mentorado, SessaoStatus.CONCLUIDA))
                .thenReturn(List.of(sessao1, sessao2));
        when(materialMentoradoRepository.findByMentoradoAndMaterial_Mentor(mentorado, mentor))
                .thenReturn(List.of(assoc1, assoc2));

        List<HistoricoSessaoDTO> resultado = historicoService.listarHistorico(mentorado, null);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());

        HistoricoSessaoDTO dto1 = resultado.get(0);
        assertEquals(sessao1.getId(), dto1.getId());
        assertEquals(sessao1.getObservacoes(), dto1.getDescricao());
        assertEquals(mentor.getId(), dto1.getMentor().getId());
        assertEquals(mentor.getName(), dto1.getMentor().getNome());
        assertEquals(2, dto1.getMateriais().size());
        assertEquals(material1.getId(), dto1.getMateriais().get(0).getId());

        verify(sessaoRepository, times(1)).findByMentoradoAndStatusOrderByDataHoraInicioDesc(mentorado, SessaoStatus.CONCLUIDA);
        verify(materialMentoradoRepository, times(2)).findByMentoradoAndMaterial_Mentor(mentorado, mentor);
    }

    @Test
    void deveListarHistoricoComFiltroDeMentor() {
        Long mentorId = 2L;
        when(userRepository.findById(mentorId)).thenReturn(Optional.of(mentor));
        when(sessaoRepository.findByMentoradoAndMentorAndStatusOrderByDataHoraInicioDesc(mentorado, mentor, SessaoStatus.CONCLUIDA))
                .thenReturn(List.of(sessao1));
        when(materialMentoradoRepository.findByMentoradoAndMaterial_Mentor(mentorado, mentor))
                .thenReturn(List.of(assoc1));

        List<HistoricoSessaoDTO> resultado = historicoService.listarHistorico(mentorado, mentorId);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(sessao1.getId(), resultado.get(0).getId());
        assertEquals(1, resultado.get(0).getMateriais().size());

        verify(userRepository, times(1)).findById(mentorId);
        verify(sessaoRepository, times(1)).findByMentoradoAndMentorAndStatusOrderByDataHoraInicioDesc(mentorado, mentor, SessaoStatus.CONCLUIDA);
    }

    @Test
    void deveLancarExcecaoQuandoMentorNaoEncontrado() {
        Long mentorId = 99L;
        when(userRepository.findById(mentorId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> historicoService.listarHistorico(mentorado, mentorId));

        verify(userRepository, times(1)).findById(mentorId);
        verify(sessaoRepository, never()).findByMentoradoAndMentorAndStatusOrderByDataHoraInicioDesc(any(), any(), any());
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHaSessoes() {
        when(sessaoRepository.findByMentoradoAndStatusOrderByDataHoraInicioDesc(mentorado, SessaoStatus.CONCLUIDA))
                .thenReturn(List.of());

        List<HistoricoSessaoDTO> resultado = historicoService.listarHistorico(mentorado, null);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());

        verify(materialMentoradoRepository, never()).findByMentoradoAndMaterial_Mentor(any(), any());
    }

    @Test
    void deveIncluirMateriaisCorretamente() {
        when(sessaoRepository.findByMentoradoAndStatusOrderByDataHoraInicioDesc(mentorado, SessaoStatus.CONCLUIDA))
                .thenReturn(List.of(sessao1));
        when(materialMentoradoRepository.findByMentoradoAndMaterial_Mentor(mentorado, mentor))
                .thenReturn(List.of(assoc1, assoc2));

        List<HistoricoSessaoDTO> resultado = historicoService.listarHistorico(mentorado, null);

        assertEquals(1, resultado.size());
        assertEquals(2, resultado.get(0).getMateriais().size());
        assertEquals(material1.getTitulo(), resultado.get(0).getMateriais().get(0).getTitulo());
        assertEquals(material2.getTitulo(), resultado.get(0).getMateriais().get(1).getTitulo());
    }
}
