package com.mentoria.agil.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sessao")
public class Sessao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "mentor_id", nullable = false)
    private User mentor;

    @ManyToOne
    @JoinColumn(name = "mentorado_id", nullable = false)
    private User mentorado;

    @Column(name = "data_hora_inicio", nullable = false)
    private LocalDateTime dataHoraInicio;

    @Column(name = "data_hora_fim", nullable = false)
    private LocalDateTime dataHoraFim;

    @Column(length = 1000)
    private String observacoes; 

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessaoStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "formato")
    private FormatoSessao formato;

    @Column(name = "link_reuniao")
    private String linkReuniao; // ONLINE

    @Column(name = "endereco")
    private String endereco; // PRESENCIAL

    @OneToOne
    @JoinColumn(name = "mentoria_request_id")
    private MentoriaRequest request; // opcional

    public Sessao() {}

    public Sessao(User mentor, User mentorado, LocalDateTime dataHoraInicio, LocalDateTime dataHoraFim, SessaoStatus status, String observacoes) {
        this.mentor = mentor;
        this.mentorado = mentorado;
        this.dataHoraInicio = dataHoraInicio;
        this.dataHoraFim = dataHoraFim;
        this.status = status;
        this.observacoes = observacoes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getMentor() {
        return mentor;
    }

    public void setMentor(User mentor) {
        this.mentor = mentor;
    }

    public User getMentorado() {
        return mentorado;
    }

    public void setMentorado(User mentorado) {
        this.mentorado = mentorado;
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

    public MentoriaRequest getRequest() {
        return request;
    }

    public void setRequest(MentoriaRequest request) {
        this.request = request;
    }

    public String getObservacoes(){
        return this.observacoes;
    }

    public void setObservacoes(String observacoes){
        this.observacoes = observacoes;
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
