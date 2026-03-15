package com.mentoria.agil.backend.interfaces.service;

import com.mentoria.agil.backend.dto.AgendamentoRequestDTO;
import com.mentoria.agil.backend.model.Sessao;
import com.mentoria.agil.backend.model.User;

public interface AgendamentoServiceInterface {
    public Sessao agendar(User mentorado, AgendamentoRequestDTO dto);
}
