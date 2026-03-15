package com.mentoria.agil.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "disponibilidades")
public class Disponibilidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "mentor_id", nullable = false)
    private User mentor;

    @Column(name = "data_hora_inicio", nullable = false)
    private LocalDateTime dataHoraInicio;

    @Column(name = "data_hora_fim", nullable = false)
    private LocalDateTime dataHoraFim;

    @Column(nullable = false)
    private Boolean disponivel = true;

    public Disponibilidade() {}

    public Disponibilidade(User mentor, LocalDateTime dataHoraInicio, LocalDateTime dataHoraFim) {
        this.mentor = mentor;
        this.dataHoraInicio = dataHoraInicio;
        this.dataHoraFim = dataHoraFim;
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

    public Boolean getDisponivel() {
        return disponivel; 
    }

    public void setDisponivel(Boolean disponivel) { 
        this.disponivel = disponivel; 
    }
}
