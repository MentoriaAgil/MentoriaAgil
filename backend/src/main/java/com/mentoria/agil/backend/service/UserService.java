package com.mentoria.agil.backend.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.mentoria.agil.backend.dto.UserDTO;
import com.mentoria.agil.backend.model.Role;
import com.mentoria.agil.backend.model.User;
import com.mentoria.agil.backend.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User salvarUsuario(UserDTO userDTO) {
    	
    	if (userRepository.existsByEmail(userDTO.getEmail())) {
    		throw new ResponseStatusException(HttpStatus.CONFLICT, "Este e-mail já está cadastrado.");
    	}
    	
    	User user = new User();
    	user.setName(userDTO.getName());
    	user.setEmail(userDTO.getEmail());
    	user.setRole(Role.VISITANTE);
    	
        // Tarefa concluída: Implementar hash de senha
        String senhaHasheada = passwordEncoder.encode(userDTO.getPassword());
        user.setPassword(senhaHasheada);
        return userRepository.save(user);
    }
    public User buscarPorEmail(String email) {
    return userRepository.findByEmail(email)
            .orElse(null); // Retorna o usuário ou null se não encontrar
    }
}