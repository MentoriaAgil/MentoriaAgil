package com.mentoria.agil.backend.dto.response;

import com.mentoria.agil.backend.model.User;

public record MentorResponseDTO(
    Long id,
    String name,
    String email,
    String especialidade,
    String areaInteresse,
    String tipoMentoria
) {
    // Construtor auxiliar pra converter User -> DTO direto
    public MentorResponseDTO(User user) {
        this(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getEspecialidade(),
            user.getAreaInteresse(),
            user.getTipoMentoria()
        );
    }
}