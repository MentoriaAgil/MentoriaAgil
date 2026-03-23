package com.mentoria.agil.backend.repository;

import java.util.List;

import com.mentoria.agil.backend.enums.MentoriaStatus;
import com.mentoria.agil.backend.model.MentoriaRequest;
import com.mentoria.agil.backend.model.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MentoriaRequestRepository extends JpaRepository<MentoriaRequest, Long> {
    // Busca por ID garante que a sincronização funcione independente do estado do
    // objeto em memória
    boolean existsByMentoradoIdAndMentorIdAndStatus(Long mentoradoId, Long mentorId, MentoriaStatus status);

    List<MentoriaRequest> findByMentorIdAndStatusOrderByCreatedAtDesc(Long mentorId, MentoriaStatus status);

    List<MentoriaRequest> findByMentoradoIdAndMentorIdAndStatus(Long mentoradoId, Long mentorId, MentoriaStatus status);

    boolean existsByMentorAndMentoradoAndStatus(User mentor, User mentorado, MentoriaStatus status);
}