package com.mentoria.agil.backend.interfaces.service;

import com.mentoria.agil.backend.model.User;
import com.mentoria.agil.backend.dto.response.HistoricoSessaoDTO;
import java.util.List;

public interface HistoricoMentoriaServiceInterface {
    public List<HistoricoSessaoDTO> listarHistorico(User mentorado, Long mentorId);
}
