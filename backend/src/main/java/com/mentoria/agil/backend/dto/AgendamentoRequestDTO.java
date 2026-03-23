// src/main/java/com/mentoria/agil/backend/dto/AgendamentoRequestDTO.java
package com.mentoria.agil.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mentoria.agil.backend.enums.FormatoSessao;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class AgendamentoRequestDTO {

    @NotNull(message = "O mentor é obrigatório")
    private Long mentorId;

    @NotNull(message = "A data de início é obrigatória")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataHoraInicio;

    @NotNull(message = "A data de fim é obrigatória")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataHoraFim;

    @NotNull(message = "O formato da sessão é obrigatório")
    private FormatoSessao formato;

    private String linkReuniao;
    private String endereco;

    // Getters e Setters...
    public Long getMentorId() {
        return mentorId;
    }

    public void setMentorId(Long mentorId) {
        this.mentorId = mentorId;
    }

    public LocalDateTime getDataHoraInicio() {
        return dataHoraInicio;
    }

    public void setDataHoraInicio(LocalDateTime dataHoraInicio) {
        this.dataHoraInicio = dataHoraInicio;
    }

    public LocalDateTime getDataHoraFim() {
        return dataHoraFim;
    }

    public void setDataHoraFim(LocalDateTime dataHoraFim) {
        this.dataHoraFim = dataHoraFim;
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