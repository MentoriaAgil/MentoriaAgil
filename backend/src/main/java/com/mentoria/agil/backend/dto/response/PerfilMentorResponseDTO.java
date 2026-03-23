package com.mentoria.agil.backend.dto.response;

import java.time.LocalDateTime;

import com.mentoria.agil.backend.model.PerfilMentor;
import com.mentoria.agil.backend.model.User;

public class PerfilMentorResponseDTO {

    private Long id;
    private Long userId;
    private String name;
    private String email;
    private String role;
    private String especializacao;
    private String experiencias;
    private String formacao;
    private String areaPrincipal;
    private String tipoMentoria;
    private String disponibilidade;
    private boolean ativo;
    private LocalDateTime createdAt;

    public PerfilMentorResponseDTO() {
    }

    public PerfilMentorResponseDTO(PerfilMentor perfil) {
        User user = perfil.getUser();
        this.id = perfil.getId();
        this.userId = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.role = user.getRole() != null ? user.getRole().name() : null;
        this.especializacao = perfil.getEspecializacao();
        this.experiencias = perfil.getExperiencias();
        this.formacao = perfil.getFormacao();
        this.areaPrincipal = perfil.getAreaPrincipal();
        this.tipoMentoria = perfil.getTipoMentoria() != null ? perfil.getTipoMentoria().name() : null;
        this.disponibilidade = perfil.getDisponibilidade() != null ? perfil.getDisponibilidade().name() : "DISPONIVEL";
        this.ativo = user.isAtivo();
        this.createdAt = user.getCreatedAt();
    }

    public PerfilMentorResponseDTO(Long id, Long userId, String name, String email,
            String role, String especializacao, String experiencias,
            String formacao, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
        this.especializacao = especializacao;
        this.experiencias = experiencias;
        this.formacao = formacao;
        this.createdAt = createdAt;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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

    public String getAreaPrincipal() {
        return areaPrincipal;
    }

    public void setAreaPrincipal(String areaPrincipal) {
        this.areaPrincipal = areaPrincipal;
    }

    public String getTipoMentoria() {
        return tipoMentoria;
    }

    public void setTipoMentoria(String tipoMentoria) {
        this.tipoMentoria = tipoMentoria;
    }

    public String getDisponibilidade() {
        return disponibilidade;
    }

    public void setDisponibilidade(String disponibilidade) {
        this.disponibilidade = disponibilidade;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}