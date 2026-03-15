package com.mentoria.agil.backend.repository;

import java.time.LocalDateTime;
import java.util.List;
import com.mentoria.agil.backend.model.User;
import com.mentoria.agil.backend.model.Sessao;
import com.mentoria.agil.backend.model.SessaoStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessaoRepository extends JpaRepository<Sessao, Long> {
    List<Sessao> findByMentoradoAndStatusOrderByDataHoraInicioDesc(User mentorado, SessaoStatus status);
    List<Sessao> findByMentoradoAndMentorAndStatusOrderByDataHoraInicioDesc(User mentorado, User mentor, SessaoStatus status);
    boolean existsByMentorAndDataHoraInicioLessThanEqualAndDataHoraFimGreaterThanEqual(User mentor, LocalDateTime dataHoraFim, LocalDateTime dataHoraInicio);
}
