package com.palangwi.soup.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

@RestController
public class HealthCheckController {

    @GetMapping("/health")
    public ResponseEntity<String> serverHealthCheck() {
        return ResponseEntity.ok("Server is running");
    }
}