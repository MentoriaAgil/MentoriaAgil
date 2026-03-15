package com.mentoria.agil.backend.controller;

import com.mentoria.agil.backend.dto.AgendamentoRequestDTO;
import com.mentoria.agil.backend.dto.response.SessaoResponseDTO;
import com.mentoria.agil.backend.interfaces.service.AgendamentoServiceInterface;
import com.mentoria.agil.backend.model.Sessao;
import com.mentoria.agil.backend.model.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sessoes")
public class AgendamentoController {

    private final AgendamentoServiceInterface agendamentoService;

    public AgendamentoController(AgendamentoServiceInterface agendamentoService) {
        this.agendamentoService = agendamentoService;
    }

    @PostMapping("/agendar")
    public ResponseEntity<SessaoResponseDTO> agendar(@Valid @RequestBody AgendamentoRequestDTO dto) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User mentorado = (User) userDetails;

        Sessao sessao = agendamentoService.agendar(mentorado, dto);
        SessaoResponseDTO response = new SessaoResponseDTO(sessao);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
