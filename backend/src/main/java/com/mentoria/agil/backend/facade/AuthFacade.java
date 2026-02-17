package com.mentoria.agil.backend.facade;

import com.mentoria.agil.backend.controller.AuthController;
import com.mentoria.agil.backend.dto.LoginDTO;
import com.mentoria.agil.backend.dto.response.LoginResponseDTO;
import com.mentoria.agil.backend.interfaces.facade.AuthFacadeInterface;
import com.mentoria.agil.backend.interfaces.service.UserServiceInterface;
import com.mentoria.agil.backend.dto.UserRequestDTO;
import com.mentoria.agil.backend.model.User;
import com.mentoria.agil.backend.service.AuthenticationService;
import com.mentoria.agil.backend.service.TokenBlacklistService;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AuthFacade implements AuthFacadeInterface {

    private final UserServiceInterface userServiceInterface;
    private final AuthenticationService authService;
    private final TokenBlacklistService blacklistService;

    public AuthFacade(UserServiceInterface userServiceInterface, AuthenticationService authService,
            TokenBlacklistService blacklistService) {
        this.userServiceInterface = userServiceInterface;
        this.authService = authService;
        this.blacklistService = blacklistService;
    }

    @Override
    public void registrarNovoUsuario(UserRequestDTO dto) {
        userServiceInterface.salvarUsuario(dto);
    }

    @Override
    public LoginResponseDTO autenticar(LoginDTO dto) {
        String token = authService.login(dto);
        User user = userServiceInterface.buscarPorEmail(dto.email());

        LoginResponseDTO response = new LoginResponseDTO(
                token, user.getName(), user.getEmail(), user.getRole());

        // Adiciona links hipertexto (HATEOAS)
        response.add(linkTo(methodOn(AuthController.class).login(dto)).withSelfRel());
        response.add(linkTo(methodOn(AuthController.class).logout(null)).withRel("logout"));

        return response;
    }

    @Override
    public void encerrarSessao(String authHeader) {
        blacklistService.invalidateToken(authHeader);
    }
}