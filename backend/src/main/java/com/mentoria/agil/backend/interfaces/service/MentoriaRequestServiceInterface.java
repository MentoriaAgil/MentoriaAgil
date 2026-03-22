package com.mentoria.agil.backend.interfaces.service;

import com.mentoria.agil.backend.dto.MentoriaRequestDTO;
import com.mentoria.agil.backend.dto.MentoriaRequestUpdateDTO;
import com.mentoria.agil.backend.model.MentoriaRequest;
import com.mentoria.agil.backend.model.User;
import java.util.List;

public interface MentoriaRequestServiceInterface {
    public MentoriaRequest createRequest(User mentorado, MentoriaRequestDTO dto);
    List<MentoriaRequest> listarPendentes(User mentor);
    MentoriaRequest atualizarStatus(Long requestId, User mentor, MentoriaRequestUpdateDTO dto);
}