package com.mentoria.agil.backend.controller;

import com.mentoria.agil.backend.dto.response.HistoricoSessaoDTO;
import com.mentoria.agil.backend.interfaces.service.HistoricoMentoriaServiceInterface;
import com.mentoria.agil.backend.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mentorias")
public class HistoricoMentoriaController {

    private final HistoricoMentoriaServiceInterface historicoService;

    public HistoricoMentoriaController(HistoricoMentoriaServiceInterface historicoService) {
        this.historicoService = historicoService;
    }

    @GetMapping("/historico")
    public ResponseEntity<List<HistoricoSessaoDTO>> listarHistorico(@RequestParam(required = false) Long mentorId) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        User mentorado = (User) userDetails;

        List<HistoricoSessaoDTO> historico = historicoService.listarHistorico(mentorado, mentorId);
        return ResponseEntity.ok(historico);
    }
}
