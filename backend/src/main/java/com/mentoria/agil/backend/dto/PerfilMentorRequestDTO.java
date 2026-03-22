package com.mentoria.agil.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class PerfilMentorRequestDTO {
    @NotBlank(message = "Especialização é obrigatória")
    private String especializacao;
    
    @NotBlank(message = "Experiência é obrigatória")
    private String experiencias;
    
    private String formacao;

    public PerfilMentorRequestDTO() {}

    public PerfilMentorRequestDTO(String especializacao, String experiencias, String formacao) { 
        this.especializacao = especializacao;
        this.experiencias = experiencias;
        this.formacao = formacao;
    }

    public String getEspecializacao() {
        return especializacao;
    }

    public void setEspecializacao(String especializacao) {
        this.especializacao = especializacao;
    }

    public String getExperiencias() {
        return experiencias;
    }

    public void setExperiencias(String experiencias) {
        this.experiencias = experiencias;
    }

    public String getFormacao() {
        return formacao;
    }

    public void setFormacao(String formacao) {
        this.formacao = formacao;
    }
}