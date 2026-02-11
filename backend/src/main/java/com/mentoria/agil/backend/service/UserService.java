package com.mentoria.agil.backend.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    public User salvarUsuario(User user) {
        // Tarefa concluída: Implementar hash de senha
        String senhaHasheada = passwordEncoder.encode(user.getPassword());
        user.setPassword(senhaHasheada);
        return userRepository.save(user);
    }
}