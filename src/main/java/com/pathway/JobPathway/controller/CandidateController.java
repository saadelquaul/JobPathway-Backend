package com.pathway.JobPathway.controller;

import com.pathway.JobPathway.dto.*;
import com.pathway.JobPathway.entity.User;
import com.pathway.JobPathway.service.CandidateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/candidate")
@RequiredArgsConstructor
public class CandidateController {

    private final CandidateService candidateService;

    @GetMapping("/profile")
    public ResponseEntity<CandidateProfileResponse> getProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(candidateService.getProfile(user));
    }

    @PutMapping("/profile")
    public ResponseEntity<CandidateProfileResponse> updateProfile(
            @AuthenticationPrincipal User user,
            @RequestBody CandidateProfileUpdateRequest request) {
        return ResponseEntity.ok(candidateService.updateProfile(user, request));
    }

    // ---- Resume ----

    @PostMapping("/resume")
    public ResponseEntity<Map<String, String>> uploadResume(
            @AuthenticationPrincipal User user,
            @RequestParam("file") MultipartFile file) {
        String url = candidateService.uploadResume(user, file);
        return ResponseEntity.ok(Map.of("url", url));
    }

    @PostMapping("/profile-picture")
    public ResponseEntity<Map<String, String>> uploadProfilePicture(
            @AuthenticationPrincipal User user,
            @RequestParam("file") MultipartFile file) {
        String url = candidateService.uploadProfilePicture(user, file);
        return ResponseEntity.ok(Map.of("url", url));
    }

    // ---- Education ----

    @PostMapping("/education")
    public ResponseEntity<EducationDTO> addEducation(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody EducationDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(candidateService.addEducation(user, dto));
    }

    @PutMapping("/education/{id}")
    public ResponseEntity<EducationDTO> updateEducation(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @Valid @RequestBody EducationDTO dto) {
        return ResponseEntity.ok(candidateService.updateEducation(user, id, dto));
    }

    @DeleteMapping("/education/{id}")
    public ResponseEntity<Void> deleteEducation(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        candidateService.deleteEducation(user, id);
        return ResponseEntity.noContent().build();
    }

    // ---- Experience ----

    @PostMapping("/experience")
    public ResponseEntity<ExperienceDTO> addExperience(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ExperienceDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(candidateService.addExperience(user, dto));
    }

    @PutMapping("/experience/{id}")
    public ResponseEntity<ExperienceDTO> updateExperience(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @Valid @RequestBody ExperienceDTO dto) {
        return ResponseEntity.ok(candidateService.updateExperience(user, id, dto));
    }

    @DeleteMapping("/experience/{id}")
    public ResponseEntity<Void> deleteExperience(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        candidateService.deleteExperience(user, id);
        return ResponseEntity.noContent().build();
    }

    // ---- Skills ----

    @PostMapping("/skills")
    public ResponseEntity<CandidateSkillDTO> addSkill(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CandidateSkillDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(candidateService.addSkill(user, dto));
    }

    @PutMapping("/skills/{id}")
    public ResponseEntity<CandidateSkillDTO> updateSkill(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @Valid @RequestBody CandidateSkillDTO dto) {
        return ResponseEntity.ok(candidateService.updateSkill(user, id, dto));
    }

    @DeleteMapping("/skills/{id}")
    public ResponseEntity<Void> deleteSkill(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        candidateService.deleteSkill(user, id);
        return ResponseEntity.noContent().build();
    }
}
