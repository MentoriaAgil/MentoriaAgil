// src/main/java/com/mentoria/agil/backend/controller/DisponibilidadeController.java
package com.mentoria.agil.backend.controller;

import com.mentoria.agil.backend.dto.DisponibilidadeRequestDTO;
import com.mentoria.agil.backend.dto.response.DisponibilidadeResponseDTO;
import com.mentoria.agil.backend.interfaces.service.DisponibilidadeServiceInterface;
import com.mentoria.agil.backend.model.Disponibilidade;
import com.mentoria.agil.backend.model.User;
import com.mentoria.agil.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/disponibilidades")
public class DisponibilidadeController {

    private final DisponibilidadeServiceInterface disponibilidadeService;
    private final UserRepository userRepository;

    public DisponibilidadeController(DisponibilidadeServiceInterface disponibilidadeService,
            UserRepository userRepository) {
        this.disponibilidadeService = disponibilidadeService;
        this.userRepository = userRepository;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('MENTOR')")
    public ResponseEntity<DisponibilidadeResponseDTO> cadastrar(@AuthenticationPrincipal User mentor,
            @Valid @RequestBody DisponibilidadeRequestDTO dto) {
        Disponibilidade disp = disponibilidadeService.cadastrar(mentor, dto);
        return new ResponseEntity<>(new DisponibilidadeResponseDTO(disp), HttpStatus.CREATED);
    }

    @GetMapping("/minhas")
    @PreAuthorize("hasAuthority('MENTOR')")
    public ResponseEntity<List<DisponibilidadeResponseDTO>> listarMinhas(@AuthenticationPrincipal User mentor) {
        List<DisponibilidadeResponseDTO> response = disponibilidadeService.listarDisponibilidadesFuturas(mentor)
                .stream().map(DisponibilidadeResponseDTO::new).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/mentor/{mentorId}")
    public ResponseEntity<List<DisponibilidadeResponseDTO>> listarPublicas(@PathVariable Long mentorId) {
        User mentor = userRepository.findById(mentorId)
                .orElseThrow(() -> new EntityNotFoundException("Mentor não encontrado"));
        List<DisponibilidadeResponseDTO> response = disponibilidadeService.listarDisponibilidadesFuturas(mentor)
                .stream().map(DisponibilidadeResponseDTO::new).toList();
        return ResponseEntity.ok(response);
    }
}