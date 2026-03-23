package com.mentoria.agil.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

import com.mentoria.agil.backend.enums.TipoMaterial;

public class MaterialRequestDTO {

    @NotBlank(message = "O título é obrigatório")
    @Size(max = 200, message = "O título deve ter no máximo 200 caracteres")
    private String titulo;

    @Size(max = 1000, message = "A descrição deve ter no máximo 1000 caracteres")
    private String descricao;

    @NotNull(message = "O tipo de material é obrigatório")
    private TipoMaterial tipo;

    @NotBlank(message = "O conteúdo ou link é obrigatório")
    @Size(max = 500, message = "O conteúdo/link deve ter no máximo 500 caracteres")
    private String conteudo;

    private List<Long> mentoradosIds;

    public MaterialRequestDTO() {
    }

    public MaterialRequestDTO(String titulo, String descricao, TipoMaterial tipo, String conteudo,
            List<Long> mentoradosIds) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.tipo = tipo;
        this.conteudo = conteudo;
        this.mentoradosIds = mentoradosIds;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public TipoMaterial getTipo() {
        return tipo;
    }

    public void setTipo(TipoMaterial tipo) {
        this.tipo = tipo;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public List<Long> getMentoradosIds() {
        return mentoradosIds;
    }

    public void setMentoradosIds(List<Long> mentoradosIds) {
        this.mentoradosIds = mentoradosIds;
    }
}
