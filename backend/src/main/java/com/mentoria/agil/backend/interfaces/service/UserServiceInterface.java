package com.mentoria.agil.backend.interfaces.service;

import java.util.List;
import com.mentoria.agil.backend.dto.UserRequestDTO;
import com.mentoria.agil.backend.dto.response.MentorResponseDTO;
import com.mentoria.agil.backend.model.User;

public interface UserServiceInterface {
    
    User salvarUsuario(UserRequestDTO dto);
    
    User buscarPorEmail(String email);

    List<MentorResponseDTO> listarMentores(String especialidade, String areaAtuacao, String tipoMentoria);
}