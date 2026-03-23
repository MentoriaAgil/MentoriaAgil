package com.mentoria.agil.backend.controller;

import com.mentoria.agil.backend.dto.AgendamentoRequestDTO;
import com.mentoria.agil.backend.dto.response.SessaoResponseDTO;
import com.mentoria.agil.backend.enums.SessaoStatus;
import com.mentoria.agil.backend.interfaces.service.AgendamentoServiceInterface;
import com.mentoria.agil.backend.model.Sessao;
import com.mentoria.agil.backend.model.User;
import com.mentoria.agil.backend.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sessoes")
public class AgendamentoController {

    private final AgendamentoServiceInterface agendamentoService;
    private final UserRepository userRepository;

    public AgendamentoController(AgendamentoServiceInterface agendamentoService, UserRepository userRepository) {
        this.agendamentoService = agendamentoService;
        this.userRepository = userRepository;
    }

    @PostMapping("/agendar")
    public ResponseEntity<SessaoResponseDTO> agendar(@Valid @RequestBody AgendamentoRequestDTO dto) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Busca o usuário de forma segura para evitar erros de cast
        User mentorado = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Usuário logado não encontrado"));

        Sessao sessao = agendamentoService.agendar(mentorado, dto);
        return new ResponseEntity<>(new SessaoResponseDTO(sessao), HttpStatus.CREATED);
    }

    @GetMapping("/mentor/pendentes")
    public ResponseEntity<List<SessaoResponseDTO>> listarSessoesPendentes(@AuthenticationPrincipal User mentor) {
        // O Spring Security injeta o mentor autenticado diretamente
        List<SessaoResponseDTO> sessoes = agendamentoService.buscarSessoesPorMentor(mentor);
        return ResponseEntity.ok(sessoes);
    }

    @GetMapping("/minhas-sessoes")
    public ResponseEntity<List<SessaoResponseDTO>> listarMinhasSessoes(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false, defaultValue = "AGENDADA") SessaoStatus status) {

        // O service já retorna DTOs, não precisa de novo mapeamento
        List<SessaoResponseDTO> response = agendamentoService.listarSessoesPorUsuario(user, status);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/mentor/minhas-sessoes")
    @PreAuthorize("hasAuthority('MENTOR')")
    public ResponseEntity<List<SessaoResponseDTO>> listarSessoesMentor(@AuthenticationPrincipal User mentor) {
        // Busca apenas as sessões do mentor logado (Isolamento)
        return ResponseEntity.ok(agendamentoService.buscarSessoesPorMentor(mentor));
    }
}
