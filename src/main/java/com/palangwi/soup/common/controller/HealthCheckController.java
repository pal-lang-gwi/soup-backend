package com.palangwi.soup.common.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/health")
public class HealthCheckController {

    @GetMapping
    public ResponseEntity<String> serverHealthCheck() {
        return ResponseEntity.ok("Server is running");
    }
}


