package com.mentoria.agil.backend.service;

import com.mentoria.agil.backend.dto.DisponibilidadeRequestDTO;
import com.mentoria.agil.backend.enums.Role;
import com.mentoria.agil.backend.exception.BusinessException;
import com.mentoria.agil.backend.model.Disponibilidade;
import com.mentoria.agil.backend.model.User;
import com.mentoria.agil.backend.repository.DisponibilidadeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
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
    void deveLancarExcecaoQuandoDataInicioNoPassado() {
        dto.setDataHoraInicio(LocalDateTime.now().minusDays(1));
        BusinessException ex = assertThrows(BusinessException.class,
                () -> disponibilidadeService.cadastrar(mentor, dto));
        assertEquals("A data de início não pode ser no passado.", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoDataFimAntesInicio() {
        dto.setDataHoraInicio(LocalDateTime.now().plusDays(2).withHour(12).withMinute(0));
        dto.setDataHoraFim(LocalDateTime.now().plusDays(2).withHour(10).withMinute(0));
        BusinessException ex = assertThrows(BusinessException.class,
                () -> disponibilidadeService.cadastrar(mentor, dto));
        assertEquals("A data de fim deve ser posterior ao início.", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoConflitoComDisponibilidadeExistente() {
        when(disponibilidadeRepository.findDisponiveisNoIntervalo(any(), any(), any()))
                .thenReturn(List.of(new Disponibilidade()));
        BusinessException ex = assertThrows(BusinessException.class,
                () -> disponibilidadeService.cadastrar(mentor, dto));
        assertEquals("Já existe uma disponibilidade cadastrada que sobrepõe este intervalo.", ex.getMessage());
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
    void deveListarDisponibilidadesFuturas() {
        List<Disponibilidade> listaEsperada = List.of(
                new Disponibilidade(mentor,
                        LocalDateTime.now().plusDays(2).withHour(10).withMinute(0),
                        LocalDateTime.now().plusDays(2).withHour(12).withMinute(0)));

        when(disponibilidadeRepository.findByMentorAndDataHoraInicioAfterAndDisponivelTrueOrderByDataHoraInicio(
                eq(mentor), any(LocalDateTime.class)))
                .thenReturn(listaEsperada);

        List<Disponibilidade> resultado = disponibilidadeService.listarDisponibilidadesFuturas(mentor);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(disponibilidadeRepository)
                .findByMentorAndDataHoraInicioAfterAndDisponivelTrueOrderByDataHoraInicio(
                        eq(mentor), any(LocalDateTime.class));
    }
}
