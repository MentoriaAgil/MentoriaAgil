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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/disponibilidades")
public class DisponibilidadeController {

    private final DisponibilidadeServiceInterface disponibilidadeService;
    private final UserRepository userRepository;

    public DisponibilidadeController(DisponibilidadeServiceInterface disponibilidadeService, UserRepository userRepository) {
        this.disponibilidadeService = disponibilidadeService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<DisponibilidadeResponseDTO> cadastrar(@Valid @RequestBody DisponibilidadeRequestDTO dto) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User mentor = (User) userDetails;

        Disponibilidade disp = disponibilidadeService.cadastrar(mentor, dto);
        DisponibilidadeResponseDTO response = new DisponibilidadeResponseDTO(disp);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/mentor/{mentorId}")
    public ResponseEntity<List<DisponibilidadeResponseDTO>> listarDisponibilidades(@PathVariable Long mentorId) {
        User mentor = userRepository.findById(mentorId)
                .orElseThrow(() -> new EntityNotFoundException("Mentor não encontrado"));

        List<Disponibilidade> disponibilidades = disponibilidadeService.listarDisponibilidadesFuturas(mentor);
        List<DisponibilidadeResponseDTO> dtos = disponibilidades.stream()
                .map(DisponibilidadeResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}
