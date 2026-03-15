package com.mentoria.agil.backend.interfaces.service;

import com.mentoria.agil.backend.dto.DisponibilidadeRequestDTO;
import com.mentoria.agil.backend.model.Disponibilidade;
import com.mentoria.agil.backend.model.User;
import java.util.List;

public interface DisponibilidadeServiceInterface {
    public Disponibilidade cadastrar(User mentor, DisponibilidadeRequestDTO dto);
    public List<Disponibilidade> listarDisponibilidadesFuturas(User mentor);
}