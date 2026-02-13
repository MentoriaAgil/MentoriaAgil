package com.mentoria.agil.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginDTO(
    @NotBlank String email,
    @NotBlank String password
) {}


