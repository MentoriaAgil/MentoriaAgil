package com.mentoria.agil.backend.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class DisponibilidadeRequestDTO {

    @NotNull(message = "Data/hora de início é obrigatória")
    @Future(message = "A data de início deve ser futura")
    private LocalDateTime dataHoraInicio;

    @NotNull(message = "Data/hora de fim é obrigatória")
    @Future(message = "A data de fim deve ser futura")
    private LocalDateTime dataHoraFim;

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
