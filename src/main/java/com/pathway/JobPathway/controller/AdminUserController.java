package com.pathway.JobPathway.controller;

import com.pathway.JobPathway.dto.ApplicationResponse;
import com.pathway.JobPathway.dto.CandidateProfileResponse;
import com.pathway.JobPathway.service.ApplicationService;
import com.pathway.JobPathway.service.CandidateService;
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
public class AdminUserController {

    private final CandidateService candidateService;
    private final ApplicationService applicationService;

    /**
     * GET /api/admin/users?page=0&size=10&sort=createdAt,desc
     * List all candidates (paginated)
     */
    @GetMapping
    public ResponseEntity<Page<CandidateProfileResponse>> getAllCandidates(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(candidateService.getAllCandidates(pageable));
    }

    /**
     * GET /api/admin/users/{candidateId}/profile
     * Get a specific candidate's full profile
     */
    @GetMapping("/{candidateId}/profile")
    public ResponseEntity<CandidateProfileResponse> getCandidateProfile(
            @PathVariable Long candidateId) {
        return ResponseEntity.ok(candidateService.getProfileById(candidateId));
    }

    /**
     * GET /api/admin/users/{candidateId}/applications?page=0&size=10
     * Get all applications for a specific candidate
     */
    @GetMapping("/{candidateId}/applications")
    public ResponseEntity<Page<ApplicationResponse>> getCandidateApplications(
            @PathVariable Long candidateId,
            @PageableDefault(size = 10, sort = "appliedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(applicationService.getApplicationsByCandidateId(candidateId, pageable));
    }
}
