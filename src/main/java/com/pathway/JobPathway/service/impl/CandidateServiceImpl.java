package com.pathway.JobPathway.service.impl;

import com.pathway.JobPathway.dto.*;
import com.pathway.JobPathway.entity.*;
import com.pathway.JobPathway.exception.ResourceNotFoundException;
import com.pathway.JobPathway.repository.*;
import com.pathway.JobPathway.service.CandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CandidateServiceImpl implements CandidateService {

    private final CandidateRepository candidateRepository;
    private final EducationRepository educationRepository;
    private final ExperienceRepository experienceRepository;
    private final CandidateSkillRepository candidateSkillRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public CandidateProfileResponse getProfile(User user) {
        Candidate candidate = getCandidateByUser(user);
        return mapToProfileResponse(candidate);
    }

    @Override
    @Transactional
    public CandidateProfileResponse updateProfile(User user, CandidateProfileUpdateRequest request) {
        Candidate candidate = getCandidateByUser(user);

        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getProfilePicture() != null) {
            user.setProfilePicture(request.getProfilePicture());
        }
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getResumeUrl() != null) {
            candidate.setResumeUrl(request.getResumeUrl());
        }
        if (request.getPortfolioUrl() != null) {
            candidate.setPortfolioUrl(request.getPortfolioUrl());
        }
        if (request.getSummary() != null) {
            candidate.setSummary(request.getSummary());
        }

        candidateRepository.save(candidate);
        return mapToProfileResponse(candidate);
    }

    // ---- Education CRUD ----

    @Override
    @Transactional
    public EducationDTO addEducation(User user, EducationDTO dto) {
        Candidate candidate = getCandidateByUser(user);
        Education education = Education.builder()
                .candidate(candidate)
                .schoolName(dto.getSchoolName())
                .degree(dto.getDegree())
                .field(dto.getField())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .build();
        educationRepository.save(education);
        return mapEducation(education);
    }

    @Override
    @Transactional
    public EducationDTO updateEducation(User user, Long educationId, EducationDTO dto) {
        Candidate candidate = getCandidateByUser(user);
        Education education = educationRepository.findById(educationId)
                .orElseThrow(() -> new ResourceNotFoundException("Education not found with id: " + educationId));
        validateOwnership(education.getCandidate().getId(), candidate.getId(), "Education");

        education.setSchoolName(dto.getSchoolName());
        education.setDegree(dto.getDegree());
        education.setField(dto.getField());
        education.setStartDate(dto.getStartDate());
        education.setEndDate(dto.getEndDate());
        educationRepository.save(education);
        return mapEducation(education);
    }

    @Override
    @Transactional
    public void deleteEducation(User user, Long educationId) {
        Candidate candidate = getCandidateByUser(user);
        Education education = educationRepository.findById(educationId)
                .orElseThrow(() -> new ResourceNotFoundException("Education not found with id: " + educationId));
        validateOwnership(education.getCandidate().getId(), candidate.getId(), "Education");
        educationRepository.delete(education);
    }

    // ---- Experience CRUD ----

    @Override
    @Transactional
    public ExperienceDTO addExperience(User user, ExperienceDTO dto) {
        Candidate candidate = getCandidateByUser(user);
        Experience experience = Experience.builder()
                .candidate(candidate)
                .title(dto.getTitle())
                .companyName(dto.getCompanyName())
                .location(dto.getLocation())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .isCurrent(dto.isCurrent())
                .description(dto.getDescription())
                .build();
        experienceRepository.save(experience);
        return mapExperience(experience);
    }

    @Override
    @Transactional
    public ExperienceDTO updateExperience(User user, Long experienceId, ExperienceDTO dto) {
        Candidate candidate = getCandidateByUser(user);
        Experience experience = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new ResourceNotFoundException("Experience not found with id: " + experienceId));
        validateOwnership(experience.getCandidate().getId(), candidate.getId(), "Experience");

        experience.setTitle(dto.getTitle());
        experience.setCompanyName(dto.getCompanyName());
        experience.setLocation(dto.getLocation());
        experience.setStartDate(dto.getStartDate());
        experience.setEndDate(dto.getEndDate());
        experience.setCurrent(dto.isCurrent());
        experience.setDescription(dto.getDescription());
        experienceRepository.save(experience);
        return mapExperience(experience);
    }

    @Override
    @Transactional
    public void deleteExperience(User user, Long experienceId) {
        Candidate candidate = getCandidateByUser(user);
        Experience experience = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new ResourceNotFoundException("Experience not found with id: " + experienceId));
        validateOwnership(experience.getCandidate().getId(), candidate.getId(), "Experience");
        experienceRepository.delete(experience);
    }

    // ---- Skills CRUD ----

    @Override
    @Transactional
    public CandidateSkillDTO addSkill(User user, CandidateSkillDTO dto) {
        Candidate candidate = getCandidateByUser(user);
        CandidateSkill skill = CandidateSkill.builder()
                .candidate(candidate)
                .name(dto.getName())
                .level(dto.getLevel())
                .build();
        candidateSkillRepository.save(skill);
        return mapSkill(skill);
    }

    @Override
    @Transactional
    public CandidateSkillDTO updateSkill(User user, Long skillId, CandidateSkillDTO dto) {
        Candidate candidate = getCandidateByUser(user);
        CandidateSkill skill = candidateSkillRepository.findById(skillId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found with id: " + skillId));
        validateOwnership(skill.getCandidate().getId(), candidate.getId(), "Skill");

        skill.setName(dto.getName());
        skill.setLevel(dto.getLevel());
        candidateSkillRepository.save(skill);
        return mapSkill(skill);
    }

    @Override
    @Transactional
    public void deleteSkill(User user, Long skillId) {
        Candidate candidate = getCandidateByUser(user);
        CandidateSkill skill = candidateSkillRepository.findById(skillId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found with id: " + skillId));
        validateOwnership(skill.getCandidate().getId(), candidate.getId(), "Skill");
        candidateSkillRepository.delete(skill);
    }

    // ---- Helpers ----

    private Candidate getCandidateByUser(User user) {
        return candidateRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate profile not found"));
    }

    private void validateOwnership(Long ownerId, Long candidateId, String resource) {
        if (!ownerId.equals(candidateId)) {
            throw new ResourceNotFoundException(resource + " does not belong to this candidate");
        }
    }

    private CandidateProfileResponse mapToProfileResponse(Candidate candidate) {
        User user = candidate.getUser();
        return CandidateProfileResponse.builder()
                .id(candidate.getId())
                .email(user.getEmail())
                .name(user.getName())
                .profilePicture(user.getProfilePicture())
                .isEnabled(candidate.isEnabled())
                .resumeUrl(candidate.getResumeUrl())
                .portfolioUrl(candidate.getPortfolioUrl())
                .summary(candidate.getSummary())
                .educations(candidate.getEducations().stream().map(this::mapEducation).collect(Collectors.toList()))
                .experiences(candidate.getExperiences().stream().map(this::mapExperience).collect(Collectors.toList()))
                .skills(candidate.getSkills().stream().map(this::mapSkill).collect(Collectors.toList()))
                .build();
    }

    private EducationDTO mapEducation(Education e) {
        return EducationDTO.builder()
                .id(e.getId())
                .schoolName(e.getSchoolName())
                .degree(e.getDegree())
                .field(e.getField())
                .startDate(e.getStartDate())
                .endDate(e.getEndDate())
                .build();
    }

    private ExperienceDTO mapExperience(Experience e) {
        return ExperienceDTO.builder()
                .id(e.getId())
                .title(e.getTitle())
                .companyName(e.getCompanyName())
                .location(e.getLocation())
                .startDate(e.getStartDate())
                .endDate(e.getEndDate())
                .isCurrent(e.isCurrent())
                .description(e.getDescription())
                .build();
    }

    private CandidateSkillDTO mapSkill(CandidateSkill s) {
        return CandidateSkillDTO.builder()
                .id(s.getId())
                .name(s.getName())
                .level(s.getLevel())
                .build();
    }
}
