package com.mentoria.agil.backend.controller;

import com.mentoria.agil.backend.dto.MentoriaRequestDTO;
import com.mentoria.agil.backend.dto.response.MentoriaResponseDTO;
import com.mentoria.agil.backend.model.MentoriaRequest;
import com.mentoria.agil.backend.model.User;
import com.mentoria.agil.backend.interfaces.service.MentoriaRequestServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mentorias")
public class MentoriaRequestController {
    private final MentoriaRequestServiceInterface requestService;

    public MentoriaRequestController(MentoriaRequestServiceInterface requestService) {
        this.requestService = requestService;
    }

    @PostMapping("/request")
    public ResponseEntity<MentoriaResponseDTO> createRequest(@Valid @RequestBody MentoriaRequestDTO dto) {
        // Obtém o usuário autenticado (mentorado autenticado)
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        User mentorado = (User) userDetails;

        MentoriaRequest request = requestService.createRequest(mentorado, dto);
        MentoriaResponseDTO response = new MentoriaResponseDTO(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
