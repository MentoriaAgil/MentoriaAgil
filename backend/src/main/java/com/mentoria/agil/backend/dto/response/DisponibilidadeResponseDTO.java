package com.mentoria.agil.backend.dto.response;

import com.mentoria.agil.backend.model.Disponibilidade;
import java.time.LocalDateTime;

public class DisponibilidadeResponseDTO {

    private Long id;
    private LocalDateTime dataHoraInicio;
    private LocalDateTime dataHoraFim;

    public DisponibilidadeResponseDTO(Disponibilidade disponibilidade) {
        this.id = disponibilidade.getId();
        this.dataHoraInicio = disponibilidade.getDataHoraInicio();
        this.dataHoraFim = disponibilidade.getDataHoraFim();
    }

    public Long getId() { 
        return id; 
    }

    public void setId(Long id) { 
        this.id = id; 
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
}
