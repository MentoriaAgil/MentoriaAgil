package com.mentoria.agil.backend.dto.response;

import com.mentoria.agil.backend.model.User;

public class MentorResumoDTO {
    private Long id;
    private String nome;

    public MentorResumoDTO(User mentor) {
        this.id = mentor.getId();
        this.nome = mentor.getName();
    }

    public Long getId(){
        return id; 
    }

    public void setId(Long id){
        this.id = id; 
    }

    public String getNome(){
        return nome; 
    }

    public void setNome(String nome){
        this.nome = nome; 
    }
}
