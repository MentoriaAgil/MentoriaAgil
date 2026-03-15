package com.mentoria.agil.backend.service;

import com.mentoria.agil.backend.dto.AgendamentoRequestDTO;
import com.mentoria.agil.backend.exception.BusinessException;
import com.mentoria.agil.backend.model.*;
import com.mentoria.agil.backend.repository.DisponibilidadeRepository;
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
class AgendamentoServiceTest {

    @Mock
    private SessaoRepository sessaoRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DisponibilidadeRepository disponibilidadeRepository;

    @Mock
    private NotificacaoService notificacaoService;

    @InjectMocks
    private AgendamentoService agendamentoService;

    private User mentor;
    private User mentorado;
    private Disponibilidade disponibilidade;
    private AgendamentoRequestDTO dto;

    @BeforeEach
    void setUp() {
        mentor = new User("Carlos", "carlos@email.com", "senha");
        mentor.setId(1L);
        mentor.setRole(Role.MENTOR);

        mentorado = new User("João", "joao@email.com", "senha");
        mentorado.setId(2L);
        mentorado.setRole(Role.ESTUDANTE);

        disponibilidade = new Disponibilidade(mentor,
                LocalDateTime.now().plusDays(2).withHour(10).withMinute(0),
                LocalDateTime.now().plusDays(2).withHour(11).withMinute(0));

        dto = new AgendamentoRequestDTO();
        dto.setMentorId(1L);
        dto.setDataHoraInicio(LocalDateTime.now().plusDays(2).withHour(10).withMinute(0));
        dto.setDataHoraFim(LocalDateTime.now().plusDays(2).withHour(11).withMinute(0));
        dto.setFormato(FormatoSessao.ONLINE);
        dto.setLinkReuniao("https://meet.google.com/abc-xyz");
    }

    @Test
    void deveAgendarComSucesso() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mentor));
        when(sessaoRepository.existsByMentorAndDataHoraInicioLessThanEqualAndDataHoraFimGreaterThanEqual(
                any(User.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(false);
        when(disponibilidadeRepository.findDisponiveisNoIntervalo(
                any(User.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(disponibilidade));
        when(sessaoRepository.save(any(Sessao.class))).thenAnswer(inv -> inv.getArgument(0));

        Sessao resultado = agendamentoService.agendar(mentorado, dto);

        assertNotNull(resultado);
        assertEquals(mentor, resultado.getMentor());
        assertEquals(mentorado, resultado.getMentorado());
        assertEquals(dto.getDataHoraInicio(), resultado.getDataHoraInicio());
        assertEquals(dto.getDataHoraFim(), resultado.getDataHoraFim());
        assertEquals(SessaoStatus.AGENDADA, resultado.getStatus());
        assertEquals(FormatoSessao.ONLINE, resultado.getFormato());
        assertEquals(dto.getLinkReuniao(), resultado.getLinkReuniao());
        assertNull(resultado.getEndereco());

        verify(userRepository, times(1)).findById(1L);
        verify(sessaoRepository, times(1)).existsByMentorAndDataHoraInicioLessThanEqualAndDataHoraFimGreaterThanEqual(
                mentor, dto.getDataHoraFim(), dto.getDataHoraInicio());
        verify(disponibilidadeRepository, times(1)).findDisponiveisNoIntervalo(
                mentor, dto.getDataHoraInicio(), dto.getDataHoraFim());
        verify(sessaoRepository, times(1)).save(any(Sessao.class));
        verify(notificacaoService, times(1)).notificarAgendamento(mentor, mentorado, resultado);
    }

    @Test
    void deveLancarExcecaoQuandoMentorNaoEncontrado() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        dto.setMentorId(99L);

        assertThrows(EntityNotFoundException.class, () -> agendamentoService.agendar(mentorado, dto));
        verify(userRepository, times(1)).findById(99L);
        verifyNoInteractions(sessaoRepository, disponibilidadeRepository, notificacaoService);
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoForMentor() {
        User usuarioComum = new User("Pedro", "pedro@email.com", "senha");
        usuarioComum.setId(3L);
        usuarioComum.setRole(Role.USER);

        when(userRepository.findById(3L)).thenReturn(Optional.of(usuarioComum));
        dto.setMentorId(3L);

        BusinessException ex = assertThrows(BusinessException.class, () -> agendamentoService.agendar(mentorado, dto));
        assertEquals("O usuário selecionado não é um mentor", ex.getMessage());
        verify(sessaoRepository, never()).existsByMentorAndDataHoraInicioLessThanEqualAndDataHoraFimGreaterThanEqual(any(), any(), any());
        verify(notificacaoService, never()).notificarAgendamento(any(), any(), any());
    }

    @Test
    void deveLancarExcecaoQuandoDataInicioNoPassado() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mentor));
        dto.setDataHoraInicio(LocalDateTime.now().minusDays(1));

        BusinessException ex = assertThrows(BusinessException.class, () -> agendamentoService.agendar(mentorado, dto));
        assertEquals("A data de início não pode ser no passado", ex.getMessage());
        verify(notificacaoService, never()).notificarAgendamento(any(), any(), any());
    }

    @Test
    void deveLancarExcecaoQuandoDataFimAntesInicio() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mentor));
        dto.setDataHoraInicio(LocalDateTime.now().plusDays(2).withHour(11).withMinute(0));
        dto.setDataHoraFim(LocalDateTime.now().plusDays(2).withHour(10).withMinute(0));

        BusinessException ex = assertThrows(BusinessException.class, () -> agendamentoService.agendar(mentorado, dto));
        assertEquals("A data de fim deve ser posterior ao início", ex.getMessage());
        verify(notificacaoService, never()).notificarAgendamento(any(), any(), any());
    }

    @Test
    void deveLancarExcecaoQuandoConflitoComOutraSessao() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mentor));
        when(sessaoRepository.existsByMentorAndDataHoraInicioLessThanEqualAndDataHoraFimGreaterThanEqual(
                mentor, dto.getDataHoraFim(), dto.getDataHoraInicio()))
                .thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class, () -> agendamentoService.agendar(mentorado, dto));
        assertEquals("Já existe uma sessão agendada neste horário", ex.getMessage());
        verify(disponibilidadeRepository, never()).findDisponiveisNoIntervalo(any(), any(), any());
        verify(notificacaoService, never()).notificarAgendamento(any(), any(), any());
    }

    @Test
    void deveLancarExcecaoQuandoMentorNaoDisponivel() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mentor));
        when(sessaoRepository.existsByMentorAndDataHoraInicioLessThanEqualAndDataHoraFimGreaterThanEqual(
                mentor, dto.getDataHoraFim(), dto.getDataHoraInicio()))
                .thenReturn(false);
        when(disponibilidadeRepository.findDisponiveisNoIntervalo(mentor, dto.getDataHoraInicio(), dto.getDataHoraFim()))
                .thenReturn(List.of());

        BusinessException ex = assertThrows(BusinessException.class, () -> agendamentoService.agendar(mentorado, dto));
        assertEquals("O mentor não possui disponibilidade neste horário", ex.getMessage());
        verify(sessaoRepository, never()).save(any());
        verify(notificacaoService, never()).notificarAgendamento(any(), any(), any());
    }

    @Test
    void deveLancarExcecaoQuandoOnlineSemLink() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mentor));
        when(sessaoRepository.existsByMentorAndDataHoraInicioLessThanEqualAndDataHoraFimGreaterThanEqual(
                mentor, dto.getDataHoraFim(), dto.getDataHoraInicio()))
                .thenReturn(false);
        when(disponibilidadeRepository.findDisponiveisNoIntervalo(mentor, dto.getDataHoraInicio(), dto.getDataHoraFim()))
                .thenReturn(List.of(disponibilidade));
        dto.setLinkReuniao(null);

        BusinessException ex = assertThrows(BusinessException.class, () -> agendamentoService.agendar(mentorado, dto));
        assertEquals("Link da reunião é obrigatório para sessões online", ex.getMessage());
        verify(sessaoRepository, never()).save(any());
        verify(notificacaoService, never()).notificarAgendamento(any(), any(), any());
    }

    @Test
    void deveLancarExcecaoQuandoPresencialSemEndereco() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mentor));
        when(sessaoRepository.existsByMentorAndDataHoraInicioLessThanEqualAndDataHoraFimGreaterThanEqual(
                mentor, dto.getDataHoraFim(), dto.getDataHoraInicio()))
                .thenReturn(false);
        when(disponibilidadeRepository.findDisponiveisNoIntervalo(mentor, dto.getDataHoraInicio(), dto.getDataHoraFim()))
                .thenReturn(List.of(disponibilidade));
        dto.setFormato(FormatoSessao.PRESENCIAL);
        dto.setEndereco(null);

        BusinessException ex = assertThrows(BusinessException.class, () -> agendamentoService.agendar(mentorado, dto));
        assertEquals("Endereço é obrigatório para sessões presenciais", ex.getMessage());
        verify(sessaoRepository, never()).save(any());
        verify(notificacaoService, never()).notificarAgendamento(any(), any(), any());
    }
}
