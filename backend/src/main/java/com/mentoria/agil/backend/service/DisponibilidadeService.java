// src/main/java/com/mentoria/agil/backend/service/DisponibilidadeService.java
package com.mentoria.agil.backend.service;

import com.mentoria.agil.backend.dto.DisponibilidadeRequestDTO;
import com.mentoria.agil.backend.exception.BusinessException;
import com.mentoria.agil.backend.interfaces.service.DisponibilidadeServiceInterface;
import com.mentoria.agil.backend.model.Disponibilidade;
import com.mentoria.agil.backend.model.User;
import com.mentoria.agil.backend.repository.DisponibilidadeRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DisponibilidadeService implements DisponibilidadeServiceInterface {

    private final DisponibilidadeRepository disponibilidadeRepository;

    public DisponibilidadeService(DisponibilidadeRepository disponibilidadeRepository) {
        this.disponibilidadeRepository = disponibilidadeRepository;
    }

    @Transactional
    public Disponibilidade cadastrar(User mentor, DisponibilidadeRequestDTO dto) {
        // Tolerância de 1 minuto para evitar erro 400 por atraso de milissegundos na
        // rede
        if (dto.getDataHoraInicio().isBefore(LocalDateTime.now().minusMinutes(1))) {
            throw new BusinessException("A data de início não pode ser no passado.");
        }

        if (dto.getDataHoraFim().isBefore(dto.getDataHoraInicio())) {
            throw new BusinessException("A data de fim deve ser posterior ao início.");
        }

        List<Disponibilidade> conflitos = disponibilidadeRepository.findDisponiveisNoIntervalo(
                mentor, dto.getDataHoraInicio(), dto.getDataHoraFim());

        if (!conflitos.isEmpty()) {
            throw new BusinessException("Já existe uma disponibilidade cadastrada que sobrepõe este intervalo.");
        }

        Disponibilidade disp = new Disponibilidade(mentor, dto.getDataHoraInicio(), dto.getDataHoraFim());
        return disponibilidadeRepository.save(disp);
    }

    public List<Disponibilidade> listarDisponibilidadesFuturas(User mentor) {
        if (mentor == null)
            return List.of();
        return disponibilidadeRepository.findByMentorAndDataHoraInicioAfterAndDisponivelTrueOrderByDataHoraInicio(
                mentor, LocalDateTime.now());
    }
}