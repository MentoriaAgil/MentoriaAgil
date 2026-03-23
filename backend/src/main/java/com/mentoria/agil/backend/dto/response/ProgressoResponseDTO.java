package com.mentoria.agil.backend.dto.response;

public record ProgressoResponseDTO(
        Long id,
        Long mentoradoId,
        String mentorNome,
        String descricao,
        java.time.LocalDateTime dataRegistro) {
}