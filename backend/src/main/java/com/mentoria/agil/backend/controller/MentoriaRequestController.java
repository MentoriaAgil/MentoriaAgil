package com.mentoria.agil.backend.controller;

import com.mentoria.agil.backend.dto.MentoriaRequestDTO;
import com.mentoria.agil.backend.dto.response.MentoriaResponseDTO;
import com.mentoria.agil.backend.model.MentoriaRequest;
import com.mentoria.agil.backend.model.User;
import com.mentoria.agil.backend.repository.UserRepository;
import com.mentoria.agil.backend.interfaces.service.MentoriaRequestServiceInterface;
import com.mentoria.agil.backend.dto.MentoriaRequestUpdateDTO;
import com.mentoria.agil.backend.dto.response.MentoriaRequestListResponseDTO;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mentorias")
public class MentoriaRequestController {
    private final MentoriaRequestServiceInterface requestService;
    private final UserRepository userRepository;

    public MentoriaRequestController(MentoriaRequestServiceInterface requestService, UserRepository userRepository) {
        this.requestService = requestService;
        this.userRepository = userRepository;
    }

    @PostMapping("/request")
    public ResponseEntity<MentoriaResponseDTO> createRequest(@Valid @RequestBody MentoriaRequestDTO dto) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User mentorado = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Usuário logado não encontrado"));

        MentoriaRequest request = requestService.createRequest(mentorado, dto);
        return new ResponseEntity<>(new MentoriaResponseDTO(request), HttpStatus.CREATED);
    }

    @GetMapping("/pendentes")
    public ResponseEntity<List<MentoriaRequestListResponseDTO>> listarPendentes() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Busca o utilizador no banco pelo email da sessão
        User mentor = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Mentor não encontrado"));

        List<MentoriaRequestListResponseDTO> response = requestService.listarPendentes(mentor)
                .stream()
                .map(MentoriaRequestListResponseDTO::new)
                .toList();

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MentoriaResponseDTO> atualizarStatus(@PathVariable Long id,
            @Valid @RequestBody MentoriaRequestUpdateDTO dto) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Procura o mentor de forma segura para evitar ClassCastException
        User mentor = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Mentor não encontrado"));

        MentoriaRequest request = requestService.atualizarStatus(id, mentor, dto);
        return ResponseEntity.ok(new MentoriaResponseDTO(request));
    }
}