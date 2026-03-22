package com.mentoria.agil.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mentoria.agil.backend.interfaces.service.PerfilMentorServiceInterface;
import com.mentoria.agil.backend.model.PerfilMentor;
import com.mentoria.agil.backend.model.User;
import com.mentoria.agil.backend.repository.PerfilMentorRepository;
import com.mentoria.agil.backend.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class PerfilMentorService implements PerfilMentorServiceInterface {

    private final PerfilMentorRepository perfilMentorRepository;
    private final UserRepository userRepository;

    public PerfilMentorService(PerfilMentorRepository perfilMentorRepository, UserRepository userRepository) {
        this.perfilMentorRepository = perfilMentorRepository;
        this.userRepository = userRepository;
    }

    @Transactional
public PerfilMentor criarPerfilMentor(User user, String especializacao, String experiencias, String formacao) {
    if (user.getPerfilMentor() != null) {
        throw new IllegalStateException("Usuário já possui perfil de mentor");
    }

    user.setRole(com.mentoria.agil.backend.model.Role.MENTOR);
    
    PerfilMentor perfil = new PerfilMentor(especializacao, experiencias, user);
    perfil.setFormacao(formacao);
    
    // Primeiro salva o perfil para ele ter um ID e não ser mais transiente
    PerfilMentor perfilSalvo = perfilMentorRepository.save(perfil);

    user.setPerfilMentor(perfilSalvo);
    userRepository.save(user);
    
    return perfilSalvo;
}

    public PerfilMentor buscarPorId(Long id) {
        return perfilMentorRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Perfil de mentor não encontrado"));
    }

    public List<PerfilMentor> listarTodos() {
        return perfilMentorRepository.findAll();
    }

    @Transactional
    public void deletar(Long id) {
        PerfilMentor perfil = buscarPorId(id);
        User user = perfil.getUser();
        user.setPerfilMentor(null);
        user.setRole(com.mentoria.agil.backend.model.Role.VISITANTE);
        userRepository.save(user);
        perfilMentorRepository.delete(perfil);
    }
    
    @Transactional
    public PerfilMentor atualizar(User user, PerfilMentor perfil) {
        if (!perfil.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("Perfil não pertence ao usuário");
        }

        userRepository.save(user);

        return perfilMentorRepository.save(perfil);
    }
}