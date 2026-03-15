package com.mentoria.agil.backend.dto;

import com.mentoria.agil.backend.model.FormatoSessao;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class AgendamentoRequestDTO {

    @NotNull(message = "ID do mentor é obrigatório")
    private Long mentorId;

    @NotNull(message = "Data/hora de início é obrigatória")
    @Future(message = "A data de início deve ser futura")
    private LocalDateTime dataHoraInicio;

    @NotNull(message = "Data/hora de fim é obrigatória")
    @Future(message = "A data de fim deve ser futura")
    private LocalDateTime dataHoraFim;

    @NotNull(message = "Formato da sessão é obrigatório")
    private FormatoSessao formato;

    @Size(max = 500, message = "O link deve ter no máximo 500 caracteres")
    private String linkReuniao; // Obrigatório quando ONLINE

    @Size(max = 500, message = "O endereço deve ter no máximo 500 caracteres")
    private String endereco; // Obrigatório quando PRESENCIAL

    public Long getMentorId() { 
        return mentorId; 
    }

    public void setMentorId(Long mentorId) { 
        this.mentorId = mentorId; 
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