package com.mentoria.agil.backend.repository;

import com.mentoria.agil.backend.model.ProgressoMentorado;
import com.mentoria.agil.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProgressoMentoradoRepository extends JpaRepository<ProgressoMentorado, Long> {
    List<ProgressoMentorado> findByMentoradoOrderByDataRegistroDesc(User mentorado);
}