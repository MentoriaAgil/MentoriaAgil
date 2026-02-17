package com.mentoria.agil.backend.dto.response;

import com.mentoria.agil.backend.model.Role;
import org.springframework.hateoas.RepresentationModel;

// Extendemos RepresentationModel para suportar a adição de links
public class LoginResponseDTO extends RepresentationModel<LoginResponseDTO> {
    private final String token;
    private final String name;
    private final String email;
    private final Role role;

    public LoginResponseDTO(String token, String name, String email, Role role) {
        this.token = token;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    // Getters necessários para a serialização JSON
    public String getToken() { return token; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public Role getRole() { return role; }
}