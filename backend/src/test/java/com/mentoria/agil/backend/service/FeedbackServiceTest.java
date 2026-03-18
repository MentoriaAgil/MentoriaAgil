package com.mentoria.agil.backend.service;

import com.mentoria.agil.backend.dto.FeedbackRequestDTO;
import com.mentoria.agil.backend.exception.BusinessException;
import com.mentoria.agil.backend.model.Feedback;
import com.mentoria.agil.backend.model.Sessao;
import com.mentoria.agil.backend.model.User;
import com.mentoria.agil.backend.repository.FeedbackRepository;
import com.mentoria.agil.backend.repository.SessaoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedbackServiceTest {

    @Mock
    private SessaoRepository sessaoRepository;

    @Mock
    private FeedbackRepository feedbackRepository;

    @InjectMocks
    private FeedbackService feedbackService;

    private User mentorado;
    private User mentor;
    private Sessao sessao;
    private FeedbackRequestDTO dto;
    private Long sessaoId = 1L;

    @BeforeEach
    void setUp() {
        mentorado = new User("João", "joao@email.com", "123");
        mentorado.setId(10L);

        mentor = new User("Maria", "maria@email.com", "456");
        mentor.setId(20L);

        sessao = new Sessao();
        sessao.setId(sessaoId);
        sessao.setMentor(mentor);
        sessao.setMentorado(mentorado);
        sessao.setDataHoraInicio(LocalDateTime.now().minusHours(2));
        sessao.setDataHoraFim(LocalDateTime.now().minusHours(1));

        dto = new FeedbackRequestDTO();
        dto.setNota(5);
        dto.setComentario("Ótima mentoria!");
    }

    @Test
    void deveCriarFeedbackComSucesso() {
        when(sessaoRepository.findById(sessaoId)).thenReturn(Optional.of(sessao));
        when(feedbackRepository.existsBySessaoId(sessaoId)).thenReturn(false);
        when(feedbackRepository.save(any(Feedback.class))).thenAnswer(inv -> inv.getArgument(0));

        Feedback resultado = feedbackService.criarFeedback(sessaoId, mentorado, dto);

        assertNotNull(resultado);
        assertEquals(sessao, resultado.getSessao());
        assertEquals(5, resultado.getNota());
        assertEquals("Ótima mentoria!", resultado.getComentario());
        verify(feedbackRepository, times(1)).save(any(Feedback.class));
    }

    @Test
    void deveLancarExcecaoQuandoSessaoNaoEncontrada() {
        when(sessaoRepository.findById(sessaoId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> feedbackService.criarFeedback(sessaoId, mentorado, dto));
        verify(feedbackRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoMentoradoNaoParticipante() {
        User outroUsuario = new User("Outro", "outro@email.com", "789");
        outroUsuario.setId(99L);
        when(sessaoRepository.findById(sessaoId)).thenReturn(Optional.of(sessao));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> feedbackService.criarFeedback(sessaoId, outroUsuario, dto));
        assertEquals("Você não tem permissão para avaliar esta sessão", ex.getMessage());
        verify(feedbackRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoSessaoNaoTerminou() {
        sessao.setDataHoraFim(LocalDateTime.now().plusHours(1));
        when(sessaoRepository.findById(sessaoId)).thenReturn(Optional.of(sessao));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> feedbackService.criarFeedback(sessaoId, mentorado, dto));
        assertEquals("A sessão ainda não terminou. A avaliação só pode ser feita após o término.", ex.getMessage());
        verify(feedbackRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoFeedbackJaExiste() {
        when(sessaoRepository.findById(sessaoId)).thenReturn(Optional.of(sessao));
        when(feedbackRepository.existsBySessaoId(sessaoId)).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> feedbackService.criarFeedback(sessaoId, mentorado, dto));
        assertEquals("Esta sessão já foi avaliada", ex.getMessage());
        verify(feedbackRepository, never()).save(any());
    }

    @Test
    void deveCalcularMediaDoMentor() {
        User mentorLocal = new User();
        mentorLocal.setId(20L);
        when(feedbackRepository.calcularMediaNotasPorMentor(mentorLocal)).thenReturn(4.5);

        Double media = feedbackService.calcularMediaMentor(mentorLocal);
        assertEquals(4.5, media);
    }

    @Test
    void deveRetornarZeroQuandoNaoHaFeedbacks() {
        User mentorLocal = new User();
        mentorLocal.setId(20L);
        when(feedbackRepository.calcularMediaNotasPorMentor(mentorLocal)).thenReturn(null);

        Double media = feedbackService.calcularMediaMentor(mentorLocal);
        assertEquals(0.0, media);
    }
}
