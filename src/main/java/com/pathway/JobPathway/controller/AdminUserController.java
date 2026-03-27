package com.pathway.JobPathway.controller;

import com.pathway.JobPathway.dto.ApplicationResponse;
import com.pathway.JobPathway.dto.CandidateProfileResponse;
import com.pathway.JobPathway.service.ApplicationService;
import com.pathway.JobPathway.service.CandidateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@Tag(name = "Admin - User Management", description = "Admin endpoints for managing candidates")
@SecurityRequirement(name = "bearer-jwt")
public class AdminUserController {

    private final CandidateService candidateService;
    private final ApplicationService applicationService;

    @GetMapping
    @Operation(summary = "Get all candidates", description = "Retrieve all candidates with pagination")
    public ResponseEntity<Page<CandidateProfileResponse>> getAllCandidates(
            @Parameter(hidden = true) @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(candidateService.getAllCandidates(pageable));
    }

    @GetMapping("/{candidateId}/profile")
    @Operation(summary = "Get candidate profile", description = "Retrieve a specific candidate's full profile")
    public ResponseEntity<CandidateProfileResponse> getCandidateProfile(
            @PathVariable Long candidateId) {
        return ResponseEntity.ok(candidateService.getProfileById(candidateId));
    }

    @GetMapping("/{candidateId}/applications")
    @Operation(summary = "Get candidate applications", description = "Retrieve all applications for a specific candidate")
    public ResponseEntity<Page<ApplicationResponse>> getCandidateApplications(
            @PathVariable Long candidateId,
            @Parameter(hidden = true) @PageableDefault(size = 10, sort = "appliedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(applicationService.getApplicationsByCandidateId(candidateId, pageable));
    }
}
