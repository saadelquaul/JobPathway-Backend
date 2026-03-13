package com.pathway.JobPathway.service.impl;

import com.pathway.JobPathway.dto.JobOfferRequest;
import com.pathway.JobPathway.dto.JobOfferResponse;
import com.pathway.JobPathway.dto.RequiredSkillDTO;
import com.pathway.JobPathway.entity.JobOffer;
import com.pathway.JobPathway.entity.RequiredSkill;
import com.pathway.JobPathway.entity.User;
import com.pathway.JobPathway.entity.enums.JobStatus;
import com.pathway.JobPathway.exception.ResourceNotFoundException;
import com.pathway.JobPathway.repository.JobOfferRepository;
import com.pathway.JobPathway.service.JobOfferService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobOfferServiceImpl implements JobOfferService {

    private final JobOfferRepository jobOfferRepository;

    @Override
    @Transactional
    public JobOfferResponse createJobOffer(User admin, JobOfferRequest request) {
        JobOffer jobOffer = JobOffer.builder()
                .admin(admin)
                .title(request.getTitle())
                .description(request.getDescription())
                .requiredEducation(request.getRequiredEducation())
                .requiredExperience(request.getRequiredExperience())
                .type(request.getType())
                .workModel(request.getWorkModel())
                .salaryRange(request.getSalaryRange())
                .status(JobStatus.OPEN)
                .location(request.getLocation())
                .createdAt(LocalDateTime.now())
                .requiredSkills(new ArrayList<>())
                .build();

        if (request.getRequiredSkills() != null) {
            for (RequiredSkillDTO skillDTO : request.getRequiredSkills()) {
                RequiredSkill skill = RequiredSkill.builder()
                        .jobOffer(jobOffer)
                        .name(skillDTO.getName())
                        .minimumLevel(skillDTO.getMinimumLevel())
                        .build();
                jobOffer.getRequiredSkills().add(skill);
            }
        }

        jobOfferRepository.save(jobOffer);
        return mapToResponse(jobOffer);
    }

    @Override
    public JobOfferResponse getJobOfferById(Long id) {
        JobOffer jobOffer = jobOfferRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job offer not found with id: " + id));
        return mapToResponse(jobOffer);
    }

    @Override
    public List<JobOfferResponse> getAllJobOffers() {
        return jobOfferRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<JobOfferResponse> getOpenJobOffers() {
        return jobOfferRepository.findByStatus(JobStatus.OPEN).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public JobOfferResponse updateJobOffer(Long id, JobOfferRequest request) {
        JobOffer jobOffer = jobOfferRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job offer not found with id: " + id));

        jobOffer.setTitle(request.getTitle());
        jobOffer.setDescription(request.getDescription());
        jobOffer.setRequiredEducation(request.getRequiredEducation());
        jobOffer.setRequiredExperience(request.getRequiredExperience());
        jobOffer.setType(request.getType());
        jobOffer.setWorkModel(request.getWorkModel());
        jobOffer.setSalaryRange(request.getSalaryRange());
        jobOffer.setLocation(request.getLocation());

        // Update required skills
        jobOffer.getRequiredSkills().clear();
        if (request.getRequiredSkills() != null) {
            for (RequiredSkillDTO skillDTO : request.getRequiredSkills()) {
                RequiredSkill skill = RequiredSkill.builder()
                        .jobOffer(jobOffer)
                        .name(skillDTO.getName())
                        .minimumLevel(skillDTO.getMinimumLevel())
                        .build();
                jobOffer.getRequiredSkills().add(skill);
            }
        }

        jobOfferRepository.save(jobOffer);
        return mapToResponse(jobOffer);
    }

    @Override
    @Transactional
    public void deleteJobOffer(Long id) {
        JobOffer jobOffer = jobOfferRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job offer not found with id: " + id));
        jobOfferRepository.delete(jobOffer);
    }

    private JobOfferResponse mapToResponse(JobOffer jobOffer) {
        List<RequiredSkillDTO> skillDTOs = jobOffer.getRequiredSkills().stream()
                .map(s -> RequiredSkillDTO.builder()
                        .id(s.getId())
                        .name(s.getName())
                        .minimumLevel(s.getMinimumLevel())
                        .build())
                .collect(Collectors.toList());

        return JobOfferResponse.builder()
                .id(jobOffer.getId())
                .title(jobOffer.getTitle())
                .description(jobOffer.getDescription())
                .requiredEducation(jobOffer.getRequiredEducation())
                .requiredExperience(jobOffer.getRequiredExperience())
                .requiredSkills(skillDTOs)
                .type(jobOffer.getType())
                .workModel(jobOffer.getWorkModel())
                .salaryRange(jobOffer.getSalaryRange())
                .status(jobOffer.getStatus())
                .location(jobOffer.getLocation())
                .createdAt(jobOffer.getCreatedAt())
                .adminEmail(jobOffer.getAdmin().getEmail())
                .build();
    }
}
