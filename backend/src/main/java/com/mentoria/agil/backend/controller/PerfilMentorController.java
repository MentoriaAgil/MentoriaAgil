package com.mentoria.agil.backend.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.mentoria.agil.backend.dto.PerfilMentorRequestDTO;
import com.mentoria.agil.backend.dto.response.PerfilMentorListResponseDTO;
import com.mentoria.agil.backend.dto.response.PerfilMentorResponseDTO;
import com.mentoria.agil.backend.interfaces.service.PerfilMentorServiceInterface;
import com.mentoria.agil.backend.model.PerfilMentor;
import com.mentoria.agil.backend.model.User;
import com.mentoria.agil.backend.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/mentors")
public class PerfilMentorController {

    private final PerfilMentorServiceInterface mentorService;
    private final UserRepository userRepository;

    public PerfilMentorController(PerfilMentorServiceInterface mentorService, UserRepository userRepository) {
        this.mentorService = mentorService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<PerfilMentorResponseDTO> criarPerfilMentor(
            @Valid @RequestBody PerfilMentorRequestDTO request) {
        UserDetails userDetails = getUsuarioAutenticado();

        // Procura o utilizador gerido pelo JPA no repositório em vez de fazer cast
        // direto
        User userAutenticado = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Utilizador não encontrado"));

        PerfilMentor perfilSalvo = mentorService.criarPerfilMentor(
                userAutenticado,
                request.getEspecializacao(),
                request.getExperiencias(),
                request.getFormacao());

        return new ResponseEntity<>(new PerfilMentorResponseDTO(perfilSalvo), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PerfilMentorListResponseDTO>> listarMentores() {
        List<PerfilMentor> mentores = mentorService.listarTodos();

        List<PerfilMentorListResponseDTO> response = mentores.stream()
                .map(PerfilMentorListResponseDTO::new)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PerfilMentorResponseDTO> buscarMentor(@PathVariable Long id) {
        PerfilMentor perfil = mentorService.buscarPorId(id);
        PerfilMentorResponseDTO response = new PerfilMentorResponseDTO(perfil);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PerfilMentorResponseDTO> atualizarMentor(
            @PathVariable Long id,
            @Valid @RequestBody PerfilMentorRequestDTO request) {

        PerfilMentor perfil = mentorService.buscarPorId(id);
        User user = perfil.getUser();

        UserDetails userDetails = getUsuarioAutenticado();

        if (!user.getEmail().equals(userDetails.getUsername())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        perfil.setEspecializacao(request.getEspecializacao());
        perfil.setExperiencias(request.getExperiencias());
        perfil.setFormacao(request.getFormacao());

        PerfilMentor perfilAtualizado = mentorService.atualizar(user, perfil);
        PerfilMentorResponseDTO response = new PerfilMentorResponseDTO(perfilAtualizado);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarMentor(@PathVariable Long id) {
        UserDetails userDetails = getUsuarioAutenticado();
        PerfilMentor perfil = mentorService.buscarPorId(id);

        // Ajuste: Removido prefixo "ROLE_" para alinhar com User.getAuthorities()
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"));

        if (!isAdmin && !perfil.getUser().getEmail().equals(userDetails.getUsername())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        mentorService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    private UserDetails getUsuarioAutenticado() {
        return (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}