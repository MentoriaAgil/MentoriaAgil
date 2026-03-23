// src/main/java/com/mentoria/agil/backend/service/AgendamentoService.java
package com.mentoria.agil.backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.mentoria.agil.backend.dto.AgendamentoRequestDTO;
import com.mentoria.agil.backend.dto.response.SessaoResponseDTO;
import com.mentoria.agil.backend.enums.*;
import com.mentoria.agil.backend.exception.BusinessException;
import com.mentoria.agil.backend.interfaces.service.AgendamentoServiceInterface;
import com.mentoria.agil.backend.model.*;
import com.mentoria.agil.backend.repository.*;
import jakarta.persistence.EntityNotFoundException;

@Service
public class AgendamentoService implements AgendamentoServiceInterface {

    private final SessaoRepository sessaoRepository;
    private final UserRepository userRepository;
    private final DisponibilidadeRepository disponibilidadeRepository;
    private final MentoriaRequestRepository requestRepository;
    private final NotificacaoService notificacaoService;

    public AgendamentoService(SessaoRepository sessaoRepository, UserRepository userRepository,
            DisponibilidadeRepository disponibilidadeRepository, MentoriaRequestRepository requestRepository,
            NotificacaoService notificacaoService) {
        this.sessaoRepository = sessaoRepository;
        this.userRepository = userRepository;
        this.disponibilidadeRepository = disponibilidadeRepository;
        this.requestRepository = requestRepository;
        this.notificacaoService = notificacaoService;
    }

    @Override
    @Transactional
    public Sessao agendar(User mentorado, AgendamentoRequestDTO dto) {
        // 1. Validações de Entrada (Devem vir ANTES das consultas ao banco para os
        // testes passarem)
        if (dto.getDataHoraInicio().isBefore(LocalDateTime.now())) {
            throw new BusinessException("A data de início não pode ser no passado");
        }
        if (dto.getDataHoraFim().isBefore(dto.getDataHoraInicio())) {
            throw new BusinessException("A data de fim deve ser posterior ao início");
        }
        if (dto.getFormato() == FormatoSessao.ONLINE
                && (dto.getLinkReuniao() == null || dto.getLinkReuniao().isBlank())) {
            throw new BusinessException("Link da reunião é obrigatório para sessões online");
        }
        if (dto.getFormato() == FormatoSessao.PRESENCIAL
                && (dto.getEndereco() == null || dto.getEndereco().isBlank())) {
            throw new BusinessException("Endereço é obrigatório para sessões presenciais");
        }

        // 2. Validações de Identidade
        User mentor = userRepository.findById(dto.getMentorId())
                .orElseThrow(() -> new EntityNotFoundException("Mentor não encontrado"));

        if (mentor.getRole() != Role.MENTOR) {
            throw new BusinessException("O usuário selecionado não é um mentor");
        }

        // 3. Validações de Disponibilidade e Conflito
        if (!disponibilidadeRepository.existsByMentorIdAndHorario(mentor.getId(), dto.getDataHoraInicio(),
                dto.getDataHoraFim())) {
            throw new BusinessException("O mentor não possui disponibilidade neste horário");
        }

        if (sessaoRepository.existsConflito(mentor.getId(), dto.getDataHoraInicio(), dto.getDataHoraFim())) {
            throw new BusinessException("Já existe uma sessão agendada neste horário");
        }

        // 4. Criação da Sessão
        MentoriaRequest request = requestRepository
                .findByMentoradoIdAndMentorIdAndStatus(mentorado.getId(), mentor.getId(), MentoriaStatus.ACCEPTED)
                .stream().findFirst()
                .orElseGet(() -> {
                    MentoriaRequest nova = new MentoriaRequest();
                    nova.setMentorado(mentorado);
                    nova.setMentor(mentor);
                    nova.setStatus(MentoriaStatus.ACCEPTED);
                    nova.setMessage("Agendamento via calendário");
                    return requestRepository.save(nova);
                });

        Sessao sessao = new Sessao();
        sessao.setMentor(mentor);
        sessao.setMentorado(mentorado);
        sessao.setDataHoraInicio(dto.getDataHoraInicio());
        sessao.setDataHoraFim(dto.getDataHoraFim());
        sessao.setFormato(dto.getFormato());
        sessao.setLinkReuniao(dto.getLinkReuniao());
        sessao.setEndereco(dto.getEndereco());
        sessao.setStatus(SessaoStatus.AGENDADA);
        sessao.setRequest(request);

        Sessao salva = sessaoRepository.save(sessao);

        // 5. Baixa na disponibilidade (Garante que o horário suma da lista do
        // mentorado)
        disponibilidadeRepository.findDisponiveisNoIntervalo(mentor, dto.getDataHoraInicio(), dto.getDataHoraFim())
                .forEach(d -> {
                    d.setDisponivel(false);
                    disponibilidadeRepository.save(d);
                });

        notificacaoService.enviarConfirmacaoAgendamento(salva);
        return salva;
    }

    @Override
    public List<SessaoResponseDTO> listarSessoesPorUsuario(User user, SessaoStatus status) {
        return sessaoRepository.findByMentoradoAndStatusOrderByDataHoraInicioDesc(user, status)
                .stream().map(SessaoResponseDTO::new).collect(Collectors.toList());
    }

    @Override
    public List<SessaoResponseDTO> buscarSessoesPorMentor(User mentor) {
        return sessaoRepository.findByMentorOrderByDataHoraInicioDesc(mentor)
                .stream().map(SessaoResponseDTO::new).collect(Collectors.toList());
    }
}