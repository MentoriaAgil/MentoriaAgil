package com.mentoria.agil.backend.controller;

import com.mentoria.agil.backend.dto.MaterialRequestDTO;
import com.mentoria.agil.backend.dto.response.MaterialResponseDTO;
import com.mentoria.agil.backend.interfaces.service.MaterialServiceInterface;
import com.mentoria.agil.backend.model.Material;
import com.mentoria.agil.backend.model.User;
import jakarta.validation.Valid;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/materiais")
public class MaterialController {
    
    private final MaterialServiceInterface materialService;

    public MaterialController(MaterialServiceInterface materialService){
        this.materialService = materialService;
    }

    @PostMapping
    public ResponseEntity<MaterialResponseDTO> criarMaterial(@Valid @RequestBody MaterialRequestDTO dto){

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User mentor = (User) userDetails;

        Material material = materialService.criarMaterial(mentor, dto);
        MaterialResponseDTO response = new MaterialResponseDTO(material);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
        
    }

    @GetMapping("/meus-materiais")
    public ResponseEntity<List<MaterialResponseDTO>> listarMateriaisDoMentorado() {

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User mentorado = (User) userDetails;

        List<MaterialResponseDTO> response = materialService.listarMateriaisPorMentorado(mentorado)
                .stream()
                .map(MaterialResponseDTO::new)
                .toList();

        return ResponseEntity.ok(response);
    }
}