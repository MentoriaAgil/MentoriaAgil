package com.mentoria.agil.backend.service;

import com.mentoria.agil.backend.dto.AgendamentoRequestDTO;
import com.mentoria.agil.backend.exception.BusinessException;
import com.mentoria.agil.backend.interfaces.service.AgendamentoServiceInterface;
import com.mentoria.agil.backend.model.*;
import com.mentoria.agil.backend.repository.DisponibilidadeRepository;
import com.mentoria.agil.backend.repository.SessaoRepository;
import com.mentoria.agil.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.LocalDateTime;

@Service
public class AgendamentoService implements AgendamentoServiceInterface{

    private final SessaoRepository sessaoRepository;
    private final UserRepository userRepository;
    private final DisponibilidadeRepository disponibilidadeRepository;
    private final NotificacaoService notificacaoService;

    public AgendamentoService(SessaoRepository sessaoRepository, 
                            UserRepository userRepository, 
                            DisponibilidadeRepository disponibilidadeRepository,
                            NotificacaoService notificacaoService) {
        this.sessaoRepository = sessaoRepository;
        this.userRepository = userRepository;
        this.disponibilidadeRepository = disponibilidadeRepository;
        this.notificacaoService = notificacaoService;
    }

    @Transactional
    public Sessao agendar(User mentorado, AgendamentoRequestDTO dto) {

        User mentor = userRepository.findById(dto.getMentorId())
                .orElseThrow(() -> new EntityNotFoundException("Mentor não encontrado"));

        if (mentor.getRole() != Role.MENTOR) {
            throw new BusinessException("O usuário selecionado não é um mentor");
        }

        if (dto.getDataHoraInicio().isBefore(LocalDateTime.now())) {
            throw new BusinessException("A data de início não pode ser no passado");
        }

        if (dto.getDataHoraFim().isBefore(dto.getDataHoraInicio())) {
            throw new BusinessException("A data de fim deve ser posterior ao início");
        }

        // Quando já existe uma sessão agendada nesse intervalo de horas
        boolean conflito = sessaoRepository.existsByMentorAndDataHoraInicioLessThanEqualAndDataHoraFimGreaterThanEqual(
                mentor, dto.getDataHoraFim(), dto.getDataHoraInicio());
        if (conflito) {
            throw new BusinessException("Já existe uma sessão agendada neste horário");
        }

        // Verifica se o mentor está disponível nesse intervalo de horas
        List<Disponibilidade> disponivel = disponibilidadeRepository.findDisponiveisNoIntervalo(
                mentor, dto.getDataHoraInicio(), dto.getDataHoraFim());
        if (disponivel.isEmpty()) {
            throw new BusinessException("O mentor não possui disponibilidade neste horário");
        }

        if (dto.getFormato() == FormatoSessao.ONLINE && (dto.getLinkReuniao() == null || dto.getLinkReuniao().isBlank())) {
            throw new BusinessException("Link da reunião é obrigatório para sessões online");
        }

        if (dto.getFormato() == FormatoSessao.PRESENCIAL && (dto.getEndereco() == null || dto.getEndereco().isBlank())) {
            throw new BusinessException("Endereço é obrigatório para sessões presenciais");
        }

        Sessao sessao = new Sessao();
        sessao.setMentor(mentor);
        sessao.setMentorado(mentorado);
        sessao.setDataHoraInicio(dto.getDataHoraInicio());
        sessao.setDataHoraFim(dto.getDataHoraFim());
        sessao.setStatus(SessaoStatus.AGENDADA);
        sessao.setFormato(dto.getFormato());

        if (dto.getFormato() == FormatoSessao.ONLINE) {
            sessao.setLinkReuniao(dto.getLinkReuniao());
        } else {
            sessao.setEndereco(dto.getEndereco());
        }

        Sessao sessaoSalva = sessaoRepository.save(sessao);
        notificacaoService.notificarAgendamento(mentor, mentorado, sessaoSalva);
        return sessaoSalva;
    }
}