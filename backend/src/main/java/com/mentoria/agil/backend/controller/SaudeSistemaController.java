package com.mentoria.agil.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class SaudeSistemaController {

    @GetMapping
    public ResponseEntity<Map<String, String>> check() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }
}