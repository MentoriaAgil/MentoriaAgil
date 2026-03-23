package com.mentoria.agil.backend.service;

import com.mentoria.agil.backend.dto.ProgressoRequestDTO;
import com.mentoria.agil.backend.dto.response.ProgressoResponseDTO;
import com.mentoria.agil.backend.exception.BusinessException;
import com.mentoria.agil.backend.model.ProgressoMentorado;
import com.mentoria.agil.backend.model.User;
import com.mentoria.agil.backend.repository.ProgressoMentoradoRepository;
import com.mentoria.agil.backend.repository.UserRepository;
import com.mentoria.agil.backend.repository.SessaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class ProgressoMentoradoService {

    private final ProgressoMentoradoRepository repository;
    private final UserRepository userRepository;
    private final SessaoRepository sessaoRepository;

    public ProgressoMentoradoService(ProgressoMentoradoRepository repository,
            UserRepository userRepository, SessaoRepository sessaoRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.sessaoRepository = sessaoRepository;
    }

    @Transactional
    public ProgressoResponseDTO registrar(User mentor, ProgressoRequestDTO dto) {
        User mentorado = userRepository.findById(dto.mentoradoId())
                .orElseThrow(() -> new EntityNotFoundException("Mentorado nao encontrado"));

        // Validação de vínculo
        boolean temVinculo = sessaoRepository.existsByMentorAndMentorado(mentor, mentorado);

        if (!temVinculo) {
            // Mensagem simplificada para evitar erros de encoding em testes
            throw new BusinessException("Inexistencia de historico de sessoes entre mentor e aluno");
        }

        ProgressoMentorado progresso = new ProgressoMentorado();
        progresso.setMentor(mentor);
        progresso.setMentorado(mentorado);
        progresso.setDescricao(dto.descricao());

        return mapToDTO(repository.save(progresso));
    }

    @Transactional
    public ProgressoResponseDTO atualizar(Long id, User mentor, String novaDescricao) {
        ProgressoMentorado p = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Registro não encontrado."));

        if (!p.getMentor().getId().equals(mentor.getId())) {
            throw new BusinessException("Ação não autorizada: Este registro pertence a outro mentor.");
        }

        p.setDescricao(novaDescricao);
        return mapToDTO(repository.save(p));
    }

    @Transactional
    public void excluir(Long id, User mentor) {
        ProgressoMentorado p = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Registro não encontrado."));

        if (!p.getMentor().getId().equals(mentor.getId())) {
            throw new BusinessException("Ação não autorizada.");
        }
        repository.delete(p);
    }

    public List<ProgressoResponseDTO> listarPorMentorado(Long mentoradoId) {
        User mentorado = userRepository.findById(mentoradoId)
                .orElseThrow(() -> new EntityNotFoundException("Mentorado não encontrado."));

        return repository.findByMentoradoOrderByDataRegistroDesc(mentorado)
                .stream().map(this::mapToDTO).toList();
    }

    private ProgressoResponseDTO mapToDTO(ProgressoMentorado p) {
        return new ProgressoResponseDTO(p.getId(), p.getMentorado().getId(),
                p.getMentor().getName(), p.getDescricao(), p.getDataRegistro());
    }
}