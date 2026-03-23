package com.mentoria.agil.backend.dto.response;

import com.mentoria.agil.backend.enums.FormatoSessao;
import com.mentoria.agil.backend.enums.SessaoStatus;
import com.mentoria.agil.backend.model.Sessao;

import java.time.LocalDateTime;

public class SessaoResponseDTO {

    private Long id;
    private Long mentorId;
    private String mentorNome;
    private Long mentoradoId;
    private String mentoradoNome;
    private LocalDateTime dataHoraInicio;
    private LocalDateTime dataHoraFim;
    private SessaoStatus status;
    private FormatoSessao formato;
    private String linkReuniao;
    private String endereco;
    private String observacoes;

    public SessaoResponseDTO(Sessao sessao) {
        this.id = sessao.getId();
        this.mentorId = sessao.getMentor().getId();
        this.mentorNome = sessao.getMentor().getName();
        this.mentoradoId = sessao.getMentorado().getId();
        this.mentoradoNome = sessao.getMentorado().getName();
        this.dataHoraInicio = sessao.getDataHoraInicio();
        this.dataHoraFim = sessao.getDataHoraFim();
        this.status = sessao.getStatus();
        this.formato = sessao.getFormato();
        this.linkReuniao = sessao.getLinkReuniao();
        this.endereco = sessao.getEndereco();
        this.observacoes = sessao.getObservacoes();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMentorId() {
        return mentorId;
    }

    public void setMentorId(Long mentorId) {
        this.mentorId = mentorId;
    }

    public String getMentorNome() {
        return mentorNome;
    }

    public void setMentorNome(String mentorNome) {
        this.mentorNome = mentorNome;
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

    public SessaoStatus getStatus() {
        return status;
    }

    public void setStatus(SessaoStatus status) {
        this.status = status;
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

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
