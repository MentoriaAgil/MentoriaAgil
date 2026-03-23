package com.mentoria.agil.backend.repository;

import java.time.LocalDateTime;
import java.util.List;
import com.mentoria.agil.backend.model.Sessao;
import com.mentoria.agil.backend.model.User;
import com.mentoria.agil.backend.enums.SessaoStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SessaoRepository extends JpaRepository<Sessao, Long> {
        @Query("SELECT s FROM Sessao s WHERE s.mentor.id = :mentorId ORDER BY s.dataHoraInicio DESC")
        List<Sessao> findByMentorId(@Param("mentorId") Long mentorId);

        @Query("SELECT COUNT(s) > 0 FROM Sessao s WHERE s.mentor.id = :mentorId AND " +
                        "((s.dataHoraInicio < :fim AND s.dataHoraFim > :inicio))")
        boolean existsConflito(@Param("mentorId") Long mentorId,
                        @Param("inicio") LocalDateTime inicio,
                        @Param("fim") LocalDateTime fim);

        List<Sessao> findByMentoradoAndMentorAndStatusOrderByDataHoraInicioDesc(User mentorado, User mentor,
                        SessaoStatus status);

        List<Sessao> findByMentorOrderByDataHoraInicioDesc(User mentor);

        List<Sessao> findByMentoradoAndStatusOrderByDataHoraInicioDesc(User mentorado,
                        com.mentoria.agil.backend.enums.SessaoStatus status);

        boolean existsByMentorAndMentorado(User mentor, User mentorado);

}