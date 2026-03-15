package com.mentoria.agil.backend.repository;

import com.mentoria.agil.backend.model.Disponibilidade;
import com.mentoria.agil.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface DisponibilidadeRepository extends JpaRepository<Disponibilidade, Long> {

    List<Disponibilidade> findByMentorAndDataHoraInicioAfterAndDisponivelTrueOrderByDataHoraInicio(User mentor, LocalDateTime agora);

    // Verifica disponibilidade para sessão no intervalo de tempo informado
    @Query("SELECT d FROM Disponibilidade d WHERE d.mentor = :mentor AND d.dataHoraInicio <= :fim AND d.dataHoraFim >= :inicio AND d.disponivel = true")
    List<Disponibilidade> findDisponiveisNoIntervalo(@Param("mentor") User mentor,
                                                     @Param("inicio") LocalDateTime inicio,
                                                     @Param("fim") LocalDateTime fim);
}
