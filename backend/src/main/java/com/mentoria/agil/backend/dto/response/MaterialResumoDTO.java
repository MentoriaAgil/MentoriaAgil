package com.mentoria.agil.backend.dto.response;

import com.mentoria.agil.backend.model.Material;
import com.mentoria.agil.backend.model.TipoMaterial;

public class MaterialResumoDTO {
    private Long id;
    private String titulo;
    private TipoMaterial tipo;
    private String conteudo;

    public MaterialResumoDTO(Material material) {
        this.id = material.getId();
        this.titulo = material.getTitulo();
        this.tipo = material.getTipo();
        this.conteudo = material.getConteudo();
    }

    public Long getId(){
        return id; 
    }

    public void setId(Long id){
        this.id = id; 
    }

    public String getTitulo(){
        return titulo; 
    }

    public void setTitulo(String titulo){
        this.titulo = titulo; 
    }

    public TipoMaterial getTipo(){ 
        return tipo; 
    }

    public void setTipo(TipoMaterial tipo){ 
        this.tipo = tipo; 
    }

    public String getConteudo(){ 
        return conteudo; 
    }

    public void setConteudo(String conteudo){ 
        this.conteudo = conteudo; 
    }
}
