// src/test/java/com/mentoria/agil/backend/service/AgendamentoServiceTest.java
package com.mentoria.agil.backend.service;

import com.mentoria.agil.backend.dto.AgendamentoRequestDTO;
import com.mentoria.agil.backend.dto.response.SessaoResponseDTO;
import com.mentoria.agil.backend.enums.*;
import com.mentoria.agil.backend.exception.BusinessException;
import com.mentoria.agil.backend.model.*;
import com.mentoria.agil.backend.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AgendamentoServiceTest {

    @Mock
    private SessaoRepository sessaoRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private DisponibilidadeRepository disponibilidadeRepository;
    @Mock
    private MentoriaRequestRepository requestRepository;
    @Mock
    private NotificacaoService notificacaoService;

    @InjectMocks
    private AgendamentoService agendamentoService;

    private User mentor;
    private User mentorado;
    private AgendamentoRequestDTO dto;

    @BeforeEach
    void setUp() {
        mentor = new User("Mentor", "mentor@test.com", "123");
        mentor.setId(1L);
        mentor.setRole(Role.MENTOR);

        mentorado = new User("Aluno", "aluno@test.com", "123");
        mentorado.setId(2L);
        mentorado.setRole(Role.USER);

        dto = new AgendamentoRequestDTO();
        dto.setMentorId(1L);
        dto.setDataHoraInicio(LocalDateTime.now().plusDays(2));
        dto.setDataHoraFim(LocalDateTime.now().plusDays(2).plusHours(1));
        dto.setFormato(FormatoSessao.ONLINE);
        dto.setLinkReuniao("http://reuniao.com");

        // Stub global para o mentor
        when(userRepository.findById(1L)).thenReturn(Optional.of(mentor));
    }

    // --- TESTES DE VALIDAÇÃO DE ENTRADA ---

    @Test
    @DisplayName("Deve lançar exceção quando a data de início for no passado")
    void deveLancarExcecaoQuandoDataInicioNoPassado() {
        dto.setDataHoraInicio(LocalDateTime.now().minusDays(1));
        BusinessException ex = assertThrows(BusinessException.class, () -> agendamentoService.agendar(mentorado, dto));
        assertEquals("A data de início não pode ser no passado", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando a data de fim for anterior ao início")
    void deveLancarExcecaoQuandoDataFimAntesInicio() {
        dto.setDataHoraFim(dto.getDataHoraInicio().minusHours(1));
        BusinessException ex = assertThrows(BusinessException.class, () -> agendamentoService.agendar(mentorado, dto));
        assertEquals("A data de fim deve ser posterior ao início", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando sessão ONLINE não tiver link")
    void deveLancarExcecaoQuandoOnlineSemLink() {
        dto.setLinkReuniao("");
        BusinessException ex = assertThrows(BusinessException.class, () -> agendamentoService.agendar(mentorado, dto));
        assertEquals("Link da reunião é obrigatório para sessões online", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando sessão PRESENCIAL não tiver endereço")
    void deveLancarExcecaoQuandoPresencialSemEndereco() {
        dto.setFormato(FormatoSessao.PRESENCIAL);
        dto.setEndereco(null);
        BusinessException ex = assertThrows(BusinessException.class, () -> agendamentoService.agendar(mentorado, dto));
        assertEquals("Endereço é obrigatório para sessões presenciais", ex.getMessage());
    }

    // --- TESTES DE IDENTIDADE E NEGÓCIO ---

    @Test
    @DisplayName("Deve lançar exceção quando o mentor não for encontrado")
    void deveLancarExcecaoQuandoMentorNaoEncontrado() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> agendamentoService.agendar(mentorado, dto));
    }

    @Test
    @DisplayName("Deve lançar exceção quando o usuário selecionado não tiver Role MENTOR")
    void deveLancarExcecaoQuandoUsuarioNaoForMentor() {
        mentor.setRole(Role.USER);
        BusinessException ex = assertThrows(BusinessException.class, () -> agendamentoService.agendar(mentorado, dto));
        assertEquals("O usuário selecionado não é um mentor", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o mentor não possuir disponibilidade no horário")
    void deveLancarExcecaoQuandoMentorNaoDisponivel() {
        when(disponibilidadeRepository.existsByMentorIdAndHorario(anyLong(), any(), any())).thenReturn(false);
        BusinessException ex = assertThrows(BusinessException.class, () -> agendamentoService.agendar(mentorado, dto));
        assertEquals("O mentor não possui disponibilidade neste horário", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando houver conflito com outra sessão já agendada")
    void deveLancarExcecaoQuandoConflitoComOutraSessao() {
        when(disponibilidadeRepository.existsByMentorIdAndHorario(anyLong(), any(), any())).thenReturn(true);
        when(sessaoRepository.existsConflito(anyLong(), any(), any())).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class, () -> agendamentoService.agendar(mentorado, dto));
        assertEquals("Já existe uma sessão agendada neste horário", ex.getMessage());
    }

    // --- TESTES DE SUCESSO ---

    @Test
    @DisplayName("Deve agendar com sucesso criando um novo vínculo de mentoria")
    void deveAgendarComSucessoNovoVinculo() {
        when(disponibilidadeRepository.existsByMentorIdAndHorario(anyLong(), any(), any())).thenReturn(true);
        when(sessaoRepository.existsConflito(anyLong(), any(), any())).thenReturn(false);
        when(requestRepository.findByMentoradoIdAndMentorIdAndStatus(anyLong(), anyLong(), any()))
                .thenReturn(Collections.emptyList());
        when(requestRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(sessaoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Sessao s = agendamentoService.agendar(mentorado, dto);

        assertNotNull(s);
        assertEquals(SessaoStatus.AGENDADA, s.getStatus());
        verify(disponibilidadeRepository).findDisponiveisNoIntervalo(any(), any(), any());
        verify(notificacaoService).enviarConfirmacaoAgendamento(any());
    }

    @Test
    @DisplayName("Deve agendar com sucesso usando um vínculo de mentoria já existente")
    void deveAgendarComSucessoUsandoRequestExistente() {
        MentoriaRequest requestExistente = new MentoriaRequest();
        requestExistente.setId(10L);
        requestExistente.setStatus(MentoriaStatus.ACCEPTED);

        when(disponibilidadeRepository.existsByMentorIdAndHorario(anyLong(), any(), any())).thenReturn(true);
        when(sessaoRepository.existsConflito(anyLong(), any(), any())).thenReturn(false);
        when(requestRepository.findByMentoradoIdAndMentorIdAndStatus(anyLong(), anyLong(), eq(MentoriaStatus.ACCEPTED)))
                .thenReturn(List.of(requestExistente));
        when(sessaoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Sessao s = agendamentoService.agendar(mentorado, dto);

        assertNotNull(s);
        assertEquals(requestExistente, s.getRequest());
        verify(requestRepository, never()).save(any(MentoriaRequest.class));
    }

    // --- TESTES DE LISTAGEM ---

    @Test
    @DisplayName("Deve listar sessões por usuário e status")
    void deveListarSessoesPorUsuario() {
        Sessao sessao = new Sessao();
        sessao.setMentor(mentor);
        sessao.setMentorado(mentorado);
        when(sessaoRepository.findByMentoradoAndStatusOrderByDataHoraInicioDesc(any(), any()))
                .thenReturn(List.of(sessao));

        List<SessaoResponseDTO> resultado = agendamentoService.listarSessoesPorUsuario(mentorado,
                SessaoStatus.AGENDADA);

        assertFalse(resultado.isEmpty());
        verify(sessaoRepository).findByMentoradoAndStatusOrderByDataHoraInicioDesc(mentorado, SessaoStatus.AGENDADA);
    }

    @Test
    @DisplayName("Deve buscar sessões por mentor")
    void deveBuscarSessoesPorMentor() {
        Sessao sessao = new Sessao();
        sessao.setMentor(mentor);
        sessao.setMentorado(mentorado);
        when(sessaoRepository.findByMentorOrderByDataHoraInicioDesc(any())).thenReturn(List.of(sessao));

        List<SessaoResponseDTO> resultado = agendamentoService.buscarSessoesPorMentor(mentor);

        assertFalse(resultado.isEmpty());
        verify(sessaoRepository).findByMentorOrderByDataHoraInicioDesc(mentor);
    }
}