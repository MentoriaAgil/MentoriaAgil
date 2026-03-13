package com.mentoria.agil.backend.service;

import com.mentoria.agil.backend.dto.response.HistoricoSessaoDTO;
import com.mentoria.agil.backend.interfaces.service.HistoricoMentoriaServiceInterface;
import com.mentoria.agil.backend.model.Sessao;
import com.mentoria.agil.backend.model.SessaoStatus;
import com.mentoria.agil.backend.model.User;
import com.mentoria.agil.backend.repository.MaterialMentoradoRepository;
import com.mentoria.agil.backend.repository.SessaoRepository;
import com.mentoria.agil.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HistoricoMentoriaService implements HistoricoMentoriaServiceInterface{

    private final SessaoRepository sessaoRepository;
    private final UserRepository userRepository;
    private final MaterialMentoradoRepository materialMentoradoRepository;

    public HistoricoMentoriaService(SessaoRepository sessaoRepository, UserRepository userRepository, 
                                            MaterialMentoradoRepository materialMentoradoRepository) {
        this.sessaoRepository = sessaoRepository;
        this.userRepository = userRepository;
        this.materialMentoradoRepository = materialMentoradoRepository;
    }

    public List<HistoricoSessaoDTO> listarHistorico(User mentorado, Long mentorId) {
        List<Sessao> sessoes;

        if (mentorId != null) {
            User mentor = userRepository.findById(mentorId)
                    .orElseThrow(() -> new EntityNotFoundException("Mentor não encontrado"));
            sessoes = sessaoRepository.findByMentoradoAndMentorAndStatusOrderByDataHoraInicioDesc(
                    mentorado, mentor, SessaoStatus.CONCLUIDA);
        } else {
            sessoes = sessaoRepository.findByMentoradoAndStatusOrderByDataHoraInicioDesc(
                    mentorado, SessaoStatus.CONCLUIDA);
        }

        return sessoes.stream()
                .map(sessao -> {
                    // Busca os materiais de apoio do mentor associados a este mentorado
                    List<com.mentoria.agil.backend.model.Material> materiais = materialMentoradoRepository
                            .findByMentoradoAndMaterial_Mentor(mentorado, sessao.getMentor())
                            .stream()
                            .map(assoc -> assoc.getMaterial())
                            .collect(Collectors.toList());
                    return new HistoricoSessaoDTO(sessao, materiais);
                })
                .collect(Collectors.toList());
    }
}
