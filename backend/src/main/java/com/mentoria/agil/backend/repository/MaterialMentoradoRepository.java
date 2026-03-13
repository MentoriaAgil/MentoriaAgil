package com.mentoria.agil.backend.repository;

import com.mentoria.agil.backend.model.Material;
import com.mentoria.agil.backend.model.MaterialMentorado;
import com.mentoria.agil.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MaterialMentoradoRepository extends JpaRepository<MaterialMentorado, Long> {
    List<MaterialMentorado> findByMentorado(User mentorado);
    List<MaterialMentorado> findByMaterial(Material material);
    List<MaterialMentorado> findByMentoradoAndMaterial_Mentor(User mentorado, User mentor);
    boolean existsByMaterialAndMentorado(Material material, User mentorado);
}
