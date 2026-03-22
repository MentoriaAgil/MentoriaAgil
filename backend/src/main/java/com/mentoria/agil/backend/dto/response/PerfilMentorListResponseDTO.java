package com.mentoria.agil.backend.dto.response;

import com.mentoria.agil.backend.model.PerfilMentor;
import com.mentoria.agil.backend.model.User;

public class PerfilMentorListResponseDTO {
    private Long id;
    private Long userId;
    private String name;
    private String email;
    private String especializacao;
    private String formacao;

    public PerfilMentorListResponseDTO() {}

    public PerfilMentorListResponseDTO(PerfilMentor perfil) {
        User user = perfil.getUser();
        this.id = perfil.getId();
        this.userId = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.especializacao = perfil.getEspecializacao();
        this.formacao = perfil.getFormacao();
    }

    public PerfilMentorListResponseDTO(Long id, String name, String email, 
                                String especializacao, String formacao) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.especializacao = especializacao;
        this.formacao = formacao;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEspecializacao() {
        return especializacao;
    }

    public void setEspecializacao(String especializacao) {
        this.especializacao = especializacao;
    }

    public String getFormacao() {
        return formacao;
    }

    public void setFormacao(String formacao) {
        this.formacao = formacao;
    }
}