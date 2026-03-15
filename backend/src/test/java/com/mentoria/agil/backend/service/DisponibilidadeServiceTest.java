package com.mentoria.agil.backend.service;

import com.mentoria.agil.backend.dto.DisponibilidadeRequestDTO;
import com.mentoria.agil.backend.exception.BusinessException;
import com.mentoria.agil.backend.model.Disponibilidade;
import com.mentoria.agil.backend.model.Role;
import com.mentoria.agil.backend.model.User;
import com.mentoria.agil.backend.repository.DisponibilidadeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DisponibilidadeServiceTest {

    @Mock
    private DisponibilidadeRepository disponibilidadeRepository;

    @InjectMocks
    private DisponibilidadeService disponibilidadeService;

    private User mentor;
    private DisponibilidadeRequestDTO dto;

    @BeforeEach
    void setUp() {
        mentor = new User("Carlos", "carlos@email.com", "senha");
        mentor.setId(1L);
        mentor.setRole(Role.MENTOR);

        dto = new DisponibilidadeRequestDTO();
        dto.setDataHoraInicio(LocalDateTime.now().plusDays(2).withHour(10).withMinute(0));
        dto.setDataHoraFim(LocalDateTime.now().plusDays(2).withHour(12).withMinute(0));
    }

    @Test
    void deveCadastrarComSucesso() {
        when(disponibilidadeRepository.findDisponiveisNoIntervalo(
                any(User.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of());
        when(disponibilidadeRepository.save(any(Disponibilidade.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Disponibilidade resultado = disponibilidadeService.cadastrar(mentor, dto);

        assertNotNull(resultado);
        assertEquals(mentor, resultado.getMentor());
        assertEquals(dto.getDataHoraInicio(), resultado.getDataHoraInicio());
        assertEquals(dto.getDataHoraFim(), resultado.getDataHoraFim());
        assertTrue(resultado.getDisponivel());

        verify(disponibilidadeRepository, times(1)).findDisponiveisNoIntervalo(
                mentor, dto.getDataHoraInicio(), dto.getDataHoraFim());
        verify(disponibilidadeRepository, times(1)).save(any(Disponibilidade.class));
    }

    @Test
    void deveLancarExcecaoQuandoDataInicioNoPassado() {
        dto.setDataHoraInicio(LocalDateTime.now().minusDays(1));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> disponibilidadeService.cadastrar(mentor, dto));
        assertEquals("A data de início não pode ser no passado", ex.getMessage());
        verify(disponibilidadeRepository, never()).findDisponiveisNoIntervalo(any(), any(), any());
    }

    @Test
    void deveLancarExcecaoQuandoDataFimAntesInicio() {
        dto.setDataHoraInicio(LocalDateTime.now().plusDays(2).withHour(12).withMinute(0));
        dto.setDataHoraFim(LocalDateTime.now().plusDays(2).withHour(10).withMinute(0));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> disponibilidadeService.cadastrar(mentor, dto));
        assertEquals("A data de fim deve ser posterior ao início", ex.getMessage());
        verify(disponibilidadeRepository, never()).findDisponiveisNoIntervalo(any(), any(), any());
    }

    @Test
    void deveLancarExcecaoQuandoConflitoComDisponibilidadeExistente() {
        Disponibilidade existente = new Disponibilidade(mentor,
                LocalDateTime.now().plusDays(2).withHour(9).withMinute(0),
                LocalDateTime.now().plusDays(2).withHour(11).withMinute(0));
        when(disponibilidadeRepository.findDisponiveisNoIntervalo(
                mentor, dto.getDataHoraInicio(), dto.getDataHoraFim()))
                .thenReturn(List.of(existente));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> disponibilidadeService.cadastrar(mentor, dto));
        assertEquals("Já existe disponibilidade cadastrada neste intervalo", ex.getMessage());
        verify(disponibilidadeRepository, never()).save(any());
    }

    @Test
    void deveListarDisponibilidadesFuturas() {
        List<Disponibilidade> listaEsperada = List.of(
                new Disponibilidade(mentor,
                        LocalDateTime.now().plusDays(2).withHour(10).withMinute(0),
                        LocalDateTime.now().plusDays(2).withHour(12).withMinute(0))
        );
        when(disponibilidadeRepository.findByMentorAndDataHoraInicioAfterAndDisponivelTrueOrderByDataHoraInicio(
                mentor, LocalDateTime.now()))
                .thenReturn(listaEsperada);

        List<Disponibilidade> resultado = disponibilidadeService.listarDisponibilidadesFuturas(mentor);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(listaEsperada, resultado);
        verify(disponibilidadeRepository, times(1))
                .findByMentorAndDataHoraInicioAfterAndDisponivelTrueOrderByDataHoraInicio(mentor, LocalDateTime.now());
    }
}
