package com.mentoria.agil.backend.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mentoria.agil.backend.dto.UserRequestDTO;
import com.mentoria.agil.backend.dto.response.MentorResponseDTO;

import com.mentoria.agil.backend.exception.EmailJaCadastradoException;
import com.mentoria.agil.backend.interfaces.service.UserServiceInterface;
import com.mentoria.agil.backend.model.Role;
import com.mentoria.agil.backend.model.User;
import com.mentoria.agil.backend.repository.PerfilMentorRepository;
import com.mentoria.agil.backend.repository.UserRepository;

@Service
public class UserService implements UserServiceInterface {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PerfilMentorRepository perfilMentorRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, PerfilMentorRepository perfilMentorRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.perfilMentorRepository = perfilMentorRepository; 
    }

    @Override
    public User salvarUsuario(UserRequestDTO dto) {
        
        if (userRepository.existsByEmail(dto.email())) {
            throw new EmailJaCadastradoException("Este e-mail já está cadastrado.");
        }

        User user = new User();
        user.setName(dto.name());    
        user.setEmail(dto.email());  
        
        user.setRole(dto.role() != null ? dto.role() : Role.VISITANTE);

        user.setPassword(passwordEncoder.encode(dto.password()));

        return userRepository.save(user);
    }

    @Override
    public User buscarPorEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
public List<MentorResponseDTO> listarMentores(String especialidade, String areaAtuacao, String tipoMentoria) {
    return perfilMentorRepository.findAll().stream()
            .filter(perfil -> perfil.getUser().isAtivo())
            .filter(perfil -> especialidade == null || perfil.getEspecializacao().equalsIgnoreCase(especialidade))
            .filter(perfil -> areaAtuacao == null || perfil.getUser().getAreaInteresse().equalsIgnoreCase(areaAtuacao))
            .filter(perfil -> tipoMentoria == null || perfil.getUser().getTipoMentoria().equalsIgnoreCase(tipoMentoria))
            .map(MentorResponseDTO::new)
            .toList();
}
}