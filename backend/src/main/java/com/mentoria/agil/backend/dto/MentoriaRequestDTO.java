// src/main/java/com/mentoria/agil/backend/dto/MentoriaRequestDTO.java

package com.mentoria.agil.backend.dto;

import com.mentoria.agil.backend.enums.FormatoSessao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class MentoriaRequestDTO {
    @NotNull
    private Long mentorId;

    @NotBlank
    private String message;
    private LocalDateTime dataHoraProposta;
    private FormatoSessao formato;
    private String linkReuniao;
    private String endereco;

    public Long getMentorId() {
        return mentorId;
    }

    public void setMentorId(Long mentorId) {
        this.mentorId = mentorId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDataHoraProposta() {
        return dataHoraProposta;
    }

    public void setDataHoraProposta(LocalDateTime dataHoraProposta) {
        this.dataHoraProposta = dataHoraProposta;
    }

    public FormatoSessao getFormato() {
        return formato;
    }

    public void setFormato(FormatoSessao formato) {
        this.formato = formato;
    }

    public String getLinkReuniao() {
        return linkReuniao;
    }

    public void setLinkReuniao(String linkReuniao) {
        this.linkReuniao = linkReuniao;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }
}