package com.pathway.JobPathway.controller;

import com.pathway.JobPathway.dto.ApplicationResponse;
import com.pathway.JobPathway.dto.ApplicationStatusUpdateRequest;
import com.pathway.JobPathway.entity.User;
import com.pathway.JobPathway.service.ApplicationService;
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
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping("/apply/{jobOfferId}")
    public ResponseEntity<ApplicationResponse> applyForJob(
            @AuthenticationPrincipal User user,
            @PathVariable Long jobOfferId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(applicationService.applyForJob(user, jobOfferId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponse> getApplicationById(@PathVariable Long id) {
        return ResponseEntity.ok(applicationService.getApplicationById(id));
    }

    @GetMapping("/my-applications")
    public ResponseEntity<Page<ApplicationResponse>> getMyApplications(
            @AuthenticationPrincipal User user,
            @PageableDefault(size = 10, sort = "appliedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(applicationService.getMyApplications(user, pageable));
    }

    @GetMapping("/job-offer/{jobOfferId}")
    public ResponseEntity<Page<ApplicationResponse>> getApplicationsByJobOffer(
            @PathVariable Long jobOfferId,
            @PageableDefault(size = 10, sort = "appliedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(applicationService.getApplicationsByJobOffer(jobOfferId, pageable));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApplicationResponse> updateApplicationStatus(
            @PathVariable Long id,
            @Valid @RequestBody ApplicationStatusUpdateRequest request) {
        return ResponseEntity.ok(applicationService.updateApplicationStatus(id, request));
    }
}
