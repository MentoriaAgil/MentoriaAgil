package com.mentoria.agil.backend.controller;

import com.mentoria.agil.backend.dto.ProgressoRequestDTO;
import com.mentoria.agil.backend.dto.response.ProgressoResponseDTO;
import com.mentoria.agil.backend.model.User;
import com.mentoria.agil.backend.service.ProgressoMentoradoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/progresso")
@PreAuthorize("hasAuthority('MENTOR')")
public class ProgressoMentoradoController {

    private final ProgressoMentoradoService service;

    public ProgressoMentoradoController(ProgressoMentoradoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ProgressoResponseDTO> registrar(@AuthenticationPrincipal User mentor,
            @Valid @RequestBody ProgressoRequestDTO dto) {
        return ResponseEntity.ok(service.registrar(mentor, dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProgressoResponseDTO> atualizar(@PathVariable Long id,
            @AuthenticationPrincipal User mentor,
            @RequestBody String descricao) {
        return ResponseEntity.ok(service.atualizar(id, mentor, descricao));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id, @AuthenticationPrincipal User mentor) {
        service.excluir(id, mentor);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/mentorado/{mentoradoId}")
    public ResponseEntity<List<ProgressoResponseDTO>> listar(@PathVariable Long mentoradoId) {
        return ResponseEntity.ok(service.listarPorMentorado(mentoradoId));
    }
}