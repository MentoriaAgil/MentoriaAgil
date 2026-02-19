package com.mentoria.agil.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mentoria.agil.backend.model.Role;
import com.mentoria.agil.backend.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    
    boolean existsByEmail(String email);
    
    Optional<User> findByEmail(String email);

    // Busca personalizada de mentores
  @Query("SELECT u FROM User u WHERE u.role = :role " +
           "AND (:especialidade IS NULL OR u.especialidade LIKE %:especialidade%) " +
           "AND (:areaInteresse IS NULL OR u.areaInteresse LIKE %:areaInteresse%) " +
           "AND (:tipoMentoria IS NULL OR u.tipoMentoria LIKE %:tipoMentoria%) " +
           "AND u.ativo = true " +
           "ORDER BY u.name ASC")
    List<User> findMentores(
        @Param("role") Role role,
        @Param("especialidade") String especialidade,
        @Param("areaInteresse") String areaInteresse,
        @Param("tipoMentoria") String tipoMentoria
    );
}