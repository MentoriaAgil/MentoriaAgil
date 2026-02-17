package com.mentoria.agil.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "mentors")
@PrimaryKeyJoinColumn(name = "user_id")  // Chave estrangeira para users, duas tabelas (user e mentor) ligadas pela mesma chave primária
public class Mentor extends User {

    @Column(nullable = false)
    private String especializacao;

    @Column(nullable = false)
    private String experiencias;

    @Column(nullable = true)
    private String formacao;

    // Construtor com campos obrigatórios
    public Mentor(String name, String email, String password, String especializacao, String experiencias) {
        super(name, email, password, Role.MENTOR);
        this.especializacao = especializacao;
        this.experiencias = experiencias;
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