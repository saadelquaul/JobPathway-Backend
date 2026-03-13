package com.pathway.JobPathway.service.impl;

import com.pathway.JobPathway.dto.ApplicationResponse;
import com.pathway.JobPathway.dto.ApplicationStatusUpdateRequest;
import com.pathway.JobPathway.entity.Application;
import com.pathway.JobPathway.entity.Candidate;
import com.pathway.JobPathway.entity.JobOffer;
import com.pathway.JobPathway.entity.User;
import com.pathway.JobPathway.entity.enums.ApplicationStatus;
import com.pathway.JobPathway.entity.enums.JobStatus;
import com.pathway.JobPathway.exception.BadRequestException;
import com.pathway.JobPathway.exception.DuplicateResourceException;
import com.pathway.JobPathway.exception.ResourceNotFoundException;
import com.pathway.JobPathway.repository.ApplicationRepository;
import com.pathway.JobPathway.repository.CandidateRepository;
import com.pathway.JobPathway.repository.JobOfferRepository;
import com.pathway.JobPathway.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final CandidateRepository candidateRepository;
    private final JobOfferRepository jobOfferRepository;

    @Override
    @Transactional
    public ApplicationResponse applyForJob(User user, Long jobOfferId) {
        Candidate candidate = candidateRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate profile not found"));

        JobOffer jobOffer = jobOfferRepository.findById(jobOfferId)
                .orElseThrow(() -> new ResourceNotFoundException("Job offer not found with id: " + jobOfferId));

        if (jobOffer.getStatus() != JobStatus.OPEN) {
            throw new BadRequestException("Cannot apply to a closed job offer");
        }

        if (applicationRepository.existsByCandidateIdAndJobOfferId(candidate.getId(), jobOfferId)) {
            throw new DuplicateResourceException("You have already applied to this job offer");
        }

        Application application = Application.builder()
                .candidate(candidate)
                .jobOffer(jobOffer)
                .status(ApplicationStatus.PENDING)
                .appliedAt(LocalDateTime.now())
                .build();

        applicationRepository.save(application);
        return mapToResponse(application);
    }

    @Override
    public List<ApplicationResponse> getMyApplications(User user) {
        Candidate candidate = candidateRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate profile not found"));

        return applicationRepository.findByCandidateId(candidate.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ApplicationResponse> getApplicationsByJobOffer(Long jobOfferId) {
        jobOfferRepository.findById(jobOfferId)
                .orElseThrow(() -> new ResourceNotFoundException("Job offer not found with id: " + jobOfferId));

        return applicationRepository.findByJobOfferId(jobOfferId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ApplicationResponse updateApplicationStatus(Long applicationId, ApplicationStatusUpdateRequest request) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + applicationId));

        if (request.getStatus() == ApplicationStatus.MEETING_SCHEDULED && request.getMeetingDate() == null) {
            throw new BadRequestException("Meeting date is required when status is MEETING_SCHEDULED");
        }

        application.setStatus(request.getStatus());
        if (request.getStatus() == ApplicationStatus.MEETING_SCHEDULED) {
            application.setMeetingDate(request.getMeetingDate());
        }

        applicationRepository.save(application);
        return mapToResponse(application);
    }

    private ApplicationResponse mapToResponse(Application application) {
        return ApplicationResponse.builder()
                .id(application.getId())
                .jobOfferId(application.getJobOffer().getId())
                .jobOfferTitle(application.getJobOffer().getTitle())
                .candidateId(application.getCandidate().getId())
                .candidateName(application.getCandidate().getUser().getName())
                .candidateEmail(application.getCandidate().getUser().getEmail())
                .status(application.getStatus())
                .appliedAt(application.getAppliedAt())
                .meetingDate(application.getMeetingDate())
                .build();
    }
}
