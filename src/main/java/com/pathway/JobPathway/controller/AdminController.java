package com.pathway.JobPathway.controller;

import com.pathway.JobPathway.dto.AdminCreateRequest;
import com.pathway.JobPathway.dto.AuthResponse;
import com.pathway.JobPathway.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AuthService authService;

    @PostMapping("/create")
    public ResponseEntity<AuthResponse> createAdmin(@Valid @RequestBody AdminCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.createAdmin(request));
    }
}
