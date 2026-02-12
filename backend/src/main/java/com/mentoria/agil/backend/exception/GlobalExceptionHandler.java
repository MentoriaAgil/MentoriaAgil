package com.mentoria.agil.backend.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(errors);
    }
    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleConflict(org.springframework.dao.DataIntegrityViolationException ex) {
    Map<String, String> error = new HashMap<>();
        // Verifica se o erro é especificamente de e-mail duplicado
        if (ex.getMessage().contains("users_email_key")) {
        error.put("error", "Este e-mail já está cadastrado no sistema.");
        } else {
        error.put("error", "Erro de integridade de dados.");
     }
    return ResponseEntity.status(org.springframework.http.HttpStatus.CONFLICT).body(error);
    }
}