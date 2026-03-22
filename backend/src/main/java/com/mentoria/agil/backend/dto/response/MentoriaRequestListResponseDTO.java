package com.mentoria.agil.backend.dto.response;

import com.mentoria.agil.backend.model.MentoriaRequest;
import com.mentoria.agil.backend.model.MentoriaStatus;
import java.time.LocalDateTime;

public class MentoriaRequestListResponseDTO {
    private Long id;
    private Long mentoradoId;
    private String mentoradoNome;
    private String mentoradoEmail;
    private String message;
    private MentoriaStatus status;
    private LocalDateTime dataSolicitacao;

    public MentoriaRequestListResponseDTO(MentoriaRequest request) {
        this.id = request.getId();
        this.mentoradoId = request.getMentorado().getId();
        this.mentoradoNome = request.getMentorado().getName();
        this.mentoradoEmail = request.getMentorado().getEmail();
        this.message = request.getMessage();
        this.status = request.getStatus();
        this.dataSolicitacao = request.getCreatedAt();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMentoradoId() {
        return mentoradoId;
    }

    public void setMentoradoId(Long mentoradoId) {
        this.mentoradoId = mentoradoId;
    }

    public String getMentoradoNome() {
        return mentoradoNome;
    }

    public void setMentoradoNome(String mentoradoNome) {
        this.mentoradoNome = mentoradoNome;
    }

    public String getMentoradoEmail() {
        return mentoradoEmail;
    }

    public void setMentoradoEmail(String mentoradoEmail) {
        this.mentoradoEmail = mentoradoEmail;
    }

    public String getMensagem() {
        return message;
    }

    public void setMensagem(String mensagem) {
        this.message = mensagem;
    }

    public MentoriaStatus getStatus() {
        return status;
    }

    public void setStatus(MentoriaStatus status) {
        this.status = status;
    }

    public LocalDateTime getDataSolicitacao() {
        return dataSolicitacao;
    }

    public void setDataSolicitacao(LocalDateTime dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }
}