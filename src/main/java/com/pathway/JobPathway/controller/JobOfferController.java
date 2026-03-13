package com.pathway.JobPathway.controller;

import com.pathway.JobPathway.dto.JobOfferRequest;
import com.pathway.JobPathway.dto.JobOfferResponse;
import com.pathway.JobPathway.entity.User;
import com.pathway.JobPathway.service.JobOfferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/job-offers")
@RequiredArgsConstructor
public class JobOfferController {

    private final JobOfferService jobOfferService;

    @PostMapping
    public ResponseEntity<JobOfferResponse> createJobOffer(
            @AuthenticationPrincipal User admin,
            @Valid @RequestBody JobOfferRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(jobOfferService.createJobOffer(admin, request));
    }

    @GetMapping
    public ResponseEntity<List<JobOfferResponse>> getAllJobOffers() {
        return ResponseEntity.ok(jobOfferService.getAllJobOffers());
    }

    @GetMapping("/open")
    public ResponseEntity<List<JobOfferResponse>> getOpenJobOffers() {
        return ResponseEntity.ok(jobOfferService.getOpenJobOffers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobOfferResponse> getJobOfferById(@PathVariable Long id) {
        return ResponseEntity.ok(jobOfferService.getJobOfferById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobOfferResponse> updateJobOffer(
            @PathVariable Long id,
            @Valid @RequestBody JobOfferRequest request) {
        return ResponseEntity.ok(jobOfferService.updateJobOffer(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJobOffer(@PathVariable Long id) {
        jobOfferService.deleteJobOffer(id);
        return ResponseEntity.noContent().build();
    }
}
