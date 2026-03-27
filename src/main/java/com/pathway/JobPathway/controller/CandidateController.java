package com.pathway.JobPathway.controller;

import com.pathway.JobPathway.dto.*;
import com.pathway.JobPathway.entity.User;
import com.pathway.JobPathway.service.CandidateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/candidate")
@RequiredArgsConstructor
@Tag(name = "Candidate Profile", description = "Candidate profile management endpoints")
@SecurityRequirement(name = "bearer-jwt")
public class CandidateController {

    private final CandidateService candidateService;

    @GetMapping("/profile")
    @Operation(summary = "Get my profile", description = "Retrieve the authenticated candidate's profile")
    public ResponseEntity<CandidateProfileResponse> getProfile(
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(candidateService.getProfile(user));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update my profile", description = "Update the authenticated candidate's profile information")
    public ResponseEntity<CandidateProfileResponse> updateProfile(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @RequestBody CandidateProfileUpdateRequest request) {
        return ResponseEntity.ok(candidateService.updateProfile(user, request));
    }

    @PostMapping("/resume")
    @Operation(summary = "Upload resume", description = "Upload or update candidate's resume")
    public ResponseEntity<UrlResponse> uploadResume(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @RequestParam("file") MultipartFile file) {
        String url = candidateService.uploadResume(user, file);
        return ResponseEntity.ok(new UrlResponse(url));
    }

    @PostMapping("/profile-picture")
    @Operation(summary = "Upload profile picture", description = "Upload or update candidate's profile picture")
    public ResponseEntity<UrlResponse> uploadProfilePicture(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @RequestParam("file") MultipartFile file) {
        String url = candidateService.uploadProfilePicture(user, file);
        return ResponseEntity.ok(new UrlResponse(url));
    }

    @PostMapping("/education")
    @Operation(summary = "Add education", description = "Add education entry to candidate's profile")
    public ResponseEntity<EducationDTO> addEducation(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @Valid @RequestBody EducationDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(candidateService.addEducation(user, dto));
    }

    @PutMapping("/education/{id}")
    @Operation(summary = "Update education", description = "Update an existing education entry")
    public ResponseEntity<EducationDTO> updateEducation(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @Valid @RequestBody EducationDTO dto) {
        return ResponseEntity.ok(candidateService.updateEducation(user, id, dto));
    }

    @DeleteMapping("/education/{id}")
    @Operation(summary = "Delete education", description = "Delete an education entry from profile")
    public ResponseEntity<Void> deleteEducation(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        candidateService.deleteEducation(user, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/experience")
    @Operation(summary = "Add experience", description = "Add work experience entry to candidate's profile")
    public ResponseEntity<ExperienceDTO> addExperience(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @Valid @RequestBody ExperienceDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(candidateService.addExperience(user, dto));
    }

    @PutMapping("/experience/{id}")
    @Operation(summary = "Update experience", description = "Update an existing work experience entry")
    public ResponseEntity<ExperienceDTO> updateExperience(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @Valid @RequestBody ExperienceDTO dto) {
        return ResponseEntity.ok(candidateService.updateExperience(user, id, dto));
    }

    @DeleteMapping("/experience/{id}")
    @Operation(summary = "Delete experience", description = "Delete a work experience entry from profile")
    public ResponseEntity<Void> deleteExperience(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        candidateService.deleteExperience(user, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/skills")
    @Operation(summary = "Add skill", description = "Add a skill to candidate's profile")
    public ResponseEntity<CandidateSkillDTO> addSkill(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @Valid @RequestBody CandidateSkillDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(candidateService.addSkill(user, dto));
    }

    @PutMapping("/skills/{id}")
    @Operation(summary = "Update skill", description = "Update an existing skill entry")
    public ResponseEntity<CandidateSkillDTO> updateSkill(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @Valid @RequestBody CandidateSkillDTO dto) {
        return ResponseEntity.ok(candidateService.updateSkill(user, id, dto));
    }

    @DeleteMapping("/skills/{id}")
    @Operation(summary = "Delete skill", description = "Delete a skill from candidate's profile")
    public ResponseEntity<Void> deleteSkill(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        candidateService.deleteSkill(user, id);
        return ResponseEntity.noContent().build();
    }
}
