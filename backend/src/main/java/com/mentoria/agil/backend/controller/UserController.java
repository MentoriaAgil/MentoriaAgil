package com.mentoria.agil.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mentoria.agil.backend.dto.response.MentorResponseDTO;
import com.mentoria.agil.backend.interfaces.service.UserServiceInterface;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserServiceInterface userService;

    public UserController(UserServiceInterface userService) {
        this.userService = userService;
    }

    @GetMapping("/mentores")
    public ResponseEntity<List<MentorResponseDTO>> listarMentores(
            @RequestParam(required = false) String especialidade,
            @RequestParam(required = false) String areaAtuacao,
            @RequestParam(required = false) String tipoMentoria) {
        
        List<MentorResponseDTO> mentores = userService.listarMentores(especialidade, areaAtuacao, tipoMentoria);
        
        return ResponseEntity.ok(mentores);
    }
}