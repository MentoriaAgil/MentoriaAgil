package com.mentoria.agil.backend.dto.response;

import com.mentoria.agil.backend.model.Sessao;
import com.mentoria.agil.backend.model.Material;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class HistoricoSessaoDTO {
    private Long id;
    private LocalDateTime dataHora;
    private String descricao;           // Observações da sessão, descrição foi utlizada para evitar repetição nos nomes de métodos em classes distintas
    private MentorResumoDTO mentor;
    private List<MaterialResumoDTO> materiais;

    public HistoricoSessaoDTO(Sessao sessao, List<Material> materiais) {
        this.id = sessao.getId();
        this.dataHora = sessao.getDataHoraInicio(); 
        this.descricao = sessao.getObservacoes();   
        this.mentor = new MentorResumoDTO(sessao.getMentor());
        this.materiais = materiais.stream()
                .map(MaterialResumoDTO::new)
                .collect(Collectors.toList());
    }

    public Long getId(){ 
        return id; 
    }

    public void setId(Long id){ 
        this.id = id; 
    }

    public LocalDateTime getDataHora(){ 
        return dataHora; 
    }

    public void setDataHora(LocalDateTime dataHora){ 
        this.dataHora = dataHora; 
    }

    public String getDescricao(){ 
        return descricao; 
    }

    public void setDescricao(String observacoes){ 
        this.descricao = observacoes; 
    }

    public MentorResumoDTO getMentor(){ 
        return mentor; 
    }

    public void setMentor(MentorResumoDTO mentor){ 
        this.mentor = mentor; 
    }

    public List<MaterialResumoDTO> getMateriais(){ 
        return materiais; 
    }

    public void setMateriais(List<MaterialResumoDTO> materiais){ 
        this.materiais = materiais; 
    }
}
