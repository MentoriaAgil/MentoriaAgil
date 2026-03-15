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
public class DisponibilidadeService implements DisponibilidadeServiceInterface{

    private final DisponibilidadeRepository disponibilidadeRepository;

    public DisponibilidadeService(DisponibilidadeRepository disponibilidadeRepository) {
        this.disponibilidadeRepository = disponibilidadeRepository;
    }

    @Transactional
    public Disponibilidade cadastrar(User mentor, DisponibilidadeRequestDTO dto) {

        if (dto.getDataHoraInicio().isBefore(LocalDateTime.now())) {
            throw new BusinessException("A data de início não pode ser no passado");
        }
        if (dto.getDataHoraFim().isBefore(dto.getDataHoraInicio())) {
            throw new BusinessException("A data de fim deve ser posterior ao início");
        }

        List<Disponibilidade> conflitos = disponibilidadeRepository.findDisponiveisNoIntervalo(
                mentor, dto.getDataHoraInicio(), dto.getDataHoraFim());
        if (!conflitos.isEmpty()) {
            throw new BusinessException("Já existe disponibilidade cadastrada neste intervalo");
        }

        Disponibilidade disp = new Disponibilidade(mentor, dto.getDataHoraInicio(), dto.getDataHoraFim());
        return disponibilidadeRepository.save(disp);
    }

    public List<Disponibilidade> listarDisponibilidadesFuturas(User mentor) {
        return disponibilidadeRepository.findByMentorAndDataHoraInicioAfterAndDisponivelTrueOrderByDataHoraInicio(
                mentor, LocalDateTime.now());
    }
}
