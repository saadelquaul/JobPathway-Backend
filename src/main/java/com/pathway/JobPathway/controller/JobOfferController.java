package com.pathway.JobPathway.controller;

import com.pathway.JobPathway.dto.JobOfferRequest;
import com.pathway.JobPathway.dto.JobOfferResponse;
import com.pathway.JobPathway.dto.JobOfferStatusRequest;
import com.pathway.JobPathway.entity.User;
import com.pathway.JobPathway.service.JobOfferService;
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
@RequestMapping("/api/job-offers")
@RequiredArgsConstructor
@Tag(name = "Job Offers", description = "Job offer management endpoints")
public class JobOfferController {

    private final JobOfferService jobOfferService;

    @PostMapping
    @Operation(summary = "Create job offer", description = "Create a new job offer (Admin only)")
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<JobOfferResponse> createJobOffer(
            @Parameter(hidden = true) @AuthenticationPrincipal User admin,
            @Valid @RequestBody JobOfferRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(jobOfferService.createJobOffer(admin, request));
    }

    @GetMapping
    @Operation(summary = "Get all job offers", description = "Retrieve all job offers with pagination")
    public ResponseEntity<Page<JobOfferResponse>> getAllJobOffers(
            @Parameter(hidden = true) @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(jobOfferService.getAllJobOffers(pageable));
    }

    @GetMapping("/open")
    @Operation(summary = "Get open job offers", description = "Retrieve all open/active job offers")
    public ResponseEntity<Page<JobOfferResponse>> getOpenJobOffers(
            @Parameter(hidden = true) @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(jobOfferService.getOpenJobOffers(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get job offer by ID", description = "Retrieve a specific job offer by its ID")
    public ResponseEntity<JobOfferResponse> getJobOfferById(@PathVariable Long id) {
        return ResponseEntity.ok(jobOfferService.getJobOfferById(id));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update job offer status", description = "Update the status of a job offer (Admin only)")
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<JobOfferResponse> updateJobOfferStatus(
            @PathVariable Long id,
            @Valid @RequestBody JobOfferStatusRequest request) {
        return ResponseEntity.ok(jobOfferService.updateJobOfferStatus(id, request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update job offer", description = "Update job offer details (Admin only)")
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<JobOfferResponse> updateJobOffer(
            @PathVariable Long id,
            @Valid @RequestBody JobOfferRequest request) {
        return ResponseEntity.ok(jobOfferService.updateJobOffer(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete job offer", description = "Delete a job offer (Admin only)")
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<Void> deleteJobOffer(@PathVariable Long id) {
        jobOfferService.deleteJobOffer(id);
        return ResponseEntity.noContent().build();
    }
}
