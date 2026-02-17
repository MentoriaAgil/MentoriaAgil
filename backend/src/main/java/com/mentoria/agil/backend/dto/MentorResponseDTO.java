package com.mentoria.agil.backend.dto;

public class MentorResponseDTO extends UserDTO {
    
    private String especializacao;
    private String experiencias;
    private String formacao;
    
     public MentorResponseDTO() {
        super();
    }

    public MentorResponseDTO(String name, String email, String password, String role, String especializacao, String experiencias, String formacao) {
        super(name, email, password, role);
        this.especializacao = especializacao;
        this.experiencias = experiencias;
        this.formacao = formacao;
    }
    
    // Getters e Setters
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
