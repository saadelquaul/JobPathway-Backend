package com.pathway.JobPathway.controller;

import com.pathway.JobPathway.dto.ApplicationResponse;
import com.pathway.JobPathway.dto.ApplicationStatusUpdateRequest;
import com.pathway.JobPathway.entity.User;
import com.pathway.JobPathway.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
@Tag(name = "Applications", description = "Application management endpoints")
@SecurityRequirement(name = "bearer-jwt")
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping("/apply/{jobOfferId}")
    @Operation(summary = "Apply for a job", description = "Submit an application for a specific job offer")
    public ResponseEntity<ApplicationResponse> applyForJob(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @PathVariable Long jobOfferId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(applicationService.applyForJob(user, jobOfferId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get application by ID", description = "Retrieve a specific application by its ID")
    public ResponseEntity<ApplicationResponse> getApplicationById(@PathVariable Long id) {
        return ResponseEntity.ok(applicationService.getApplicationById(id));
    }

    @GetMapping("/my-applications")
    @Operation(summary = "Get my applications", description = "Retrieve all applications submitted by the authenticated candidate")
    public ResponseEntity<Page<ApplicationResponse>> getMyApplications(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @Parameter(hidden = true) @PageableDefault(size = 10, sort = "appliedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(applicationService.getMyApplications(user, pageable));
    }

    @GetMapping("/job-offer/{jobOfferId}")
    @Operation(summary = "Get applications for job offer", description = "Retrieve all applications for a specific job offer")
    public ResponseEntity<Page<ApplicationResponse>> getApplicationsByJobOffer(
            @PathVariable Long jobOfferId,
            @Parameter(hidden = true) @PageableDefault(size = 10, sort = "appliedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(applicationService.getApplicationsByJobOffer(jobOfferId, pageable));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update application status", description = "Update the status of an application (Admin only)")
    public ResponseEntity<ApplicationResponse> updateApplicationStatus(
            @PathVariable Long id,
            @Valid @RequestBody ApplicationStatusUpdateRequest request) {
        return ResponseEntity.ok(applicationService.updateApplicationStatus(id, request));
    }
}
