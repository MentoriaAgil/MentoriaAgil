package com.mentoria.agil.backend.service;

import com.mentoria.agil.backend.interfaces.service.MaterialServiceInterface;
import com.mentoria.agil.backend.dto.MaterialRequestDTO;
import com.mentoria.agil.backend.exception.BusinessException;
import com.mentoria.agil.backend.model.Material;
import com.mentoria.agil.backend.model.MaterialMentorado;
import com.mentoria.agil.backend.model.User;
import com.mentoria.agil.backend.model.Role;
import com.mentoria.agil.backend.repository.MaterialMentoradoRepository;
import com.mentoria.agil.backend.repository.MaterialRepository;
import com.mentoria.agil.backend.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class MaterialService implements MaterialServiceInterface {
    
    private final MaterialRepository materialRepository;
    private final UserRepository userRepository;
    private final MaterialMentoradoRepository materialMentoradoRepository;

    public MaterialService (MaterialRepository materialRepository, UserRepository userRepository, MaterialMentoradoRepository materialMentoradoRepository){
        this.materialRepository = materialRepository;
        this.userRepository = userRepository;
        this.materialMentoradoRepository = materialMentoradoRepository;
    }

    @Transactional
    public Material criarMaterial(User mentor, MaterialRequestDTO dto){
        Material material = new Material();
        material.setTitulo(dto.getTitulo());
        material.setDescricao(dto.getDescricao());
        material.setConteudo(dto.getConteudo());
        material.setTipo(dto.getTipo());
        material.setMentor(mentor);

        materialRepository.save(material);

        Optional.ofNullable(dto.getMentoradosIds()).ifPresent(ids -> {
            for (Long mentoradoId : ids) {
                User mentorado = userRepository.findById(mentoradoId)
                        .orElseThrow(() -> new EntityNotFoundException("Mentorado não encontrado com ID: " + mentoradoId));

                if (mentorado.getRole() != Role.ESTUDANTE) {
                    throw new BusinessException("O usuário de ID " + mentoradoId + " não é um estudante");
                }

                if (!materialMentoradoRepository.existsByMaterialAndMentorado(material, mentorado)) {
                    MaterialMentorado assoc = new MaterialMentorado(material, mentorado);
                    materialMentoradoRepository.save(assoc);
                }
            }
        });
        
        return material;
    }

    public List<Material> listarMateriaisPorMentorado(User mentorado){
        return materialMentoradoRepository.findByMentorado(mentorado)
                .stream()
                .map(MaterialMentorado::getMaterial)
                .toList();
    }
}