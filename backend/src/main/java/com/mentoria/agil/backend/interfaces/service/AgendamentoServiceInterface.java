package com.mentoria.agil.backend.interfaces.service;

import java.util.List;
import com.mentoria.agil.backend.dto.AgendamentoRequestDTO;
import com.mentoria.agil.backend.dto.response.SessaoResponseDTO;
import com.mentoria.agil.backend.enums.SessaoStatus;
import com.mentoria.agil.backend.model.Sessao;
import com.mentoria.agil.backend.model.User;

public interface AgendamentoServiceInterface {
    Sessao agendar(User mentorado, AgendamentoRequestDTO dto);

    List<SessaoResponseDTO> buscarSessoesPorMentor(User mentor);

    List<SessaoResponseDTO> listarSessoesPorUsuario(User user, SessaoStatus status);
}