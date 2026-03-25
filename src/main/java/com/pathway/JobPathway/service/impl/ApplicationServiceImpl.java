package com.pathway.JobPathway.service.impl;

import com.pathway.JobPathway.dto.ApplicationResponse;
import com.pathway.JobPathway.dto.ApplicationStatusUpdateRequest;
import com.pathway.JobPathway.entity.Application;
import com.pathway.JobPathway.entity.Candidate;
import com.pathway.JobPathway.entity.JobOffer;
import com.pathway.JobPathway.entity.User;
import com.pathway.JobPathway.entity.enums.ApplicationStatus;
import com.pathway.JobPathway.entity.enums.JobStatus;
import com.pathway.JobPathway.entity.enums.NotificationType;
import com.pathway.JobPathway.exception.BadRequestException;
import com.pathway.JobPathway.exception.DuplicateResourceException;
import com.pathway.JobPathway.exception.ResourceNotFoundException;
import com.pathway.JobPathway.repository.ApplicationRepository;
import com.pathway.JobPathway.repository.CandidateRepository;
import com.pathway.JobPathway.repository.JobOfferRepository;
import com.pathway.JobPathway.service.ApplicationService;
import com.pathway.JobPathway.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final CandidateRepository candidateRepository;
    private final JobOfferRepository jobOfferRepository;
    private final NotificationService notificationService;

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

        // Notify the admin who posted the job
        User admin = jobOffer.getAdmin();
        notificationService.createNotification(
                admin,
                NotificationType.NEW_APPLICATION,
                "New Application Received",
                candidate.getUser().getName() + " applied for " + jobOffer.getTitle(),
                application.getId(),
                "APPLICATION"
        );

        return mapToResponse(application);
    }

    @Override
    public ApplicationResponse getApplicationById(Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + applicationId));
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
    public Page<ApplicationResponse> getMyApplications(User user, Pageable pageable) {
        Candidate candidate = candidateRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate profile not found"));

        return applicationRepository.findByCandidateId(candidate.getId(), pageable)
                .map(this::mapToResponse);
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
    public Page<ApplicationResponse> getApplicationsByJobOffer(Long jobOfferId, Pageable pageable) {
        jobOfferRepository.findById(jobOfferId)
                .orElseThrow(() -> new ResourceNotFoundException("Job offer not found with id: " + jobOfferId));

        return applicationRepository.findByJobOfferId(jobOfferId, pageable)
                .map(this::mapToResponse);
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

        // Notify the candidate about the status change
        User candidateUser = application.getCandidate().getUser();
        NotificationType notifType = mapStatusToNotificationType(request.getStatus());
        notificationService.createNotification(
                candidateUser,
                notifType,
                getNotificationTitle(request.getStatus()),
                buildNotificationMessage(application, request.getStatus()),
                application.getId(),
                "APPLICATION"
        );

        return mapToResponse(application);
    }

    private NotificationType mapStatusToNotificationType(ApplicationStatus status) {
        return switch (status) {
            case IN_REVIEW -> NotificationType.APPLICATION_UNDER_REVIEW;
            case MEETING_SCHEDULED -> NotificationType.MEETING_SCHEDULED;
            case APPROVED -> NotificationType.APPLICATION_APPROVED;
            case REJECTED -> NotificationType.APPLICATION_REJECTED;
            default -> NotificationType.APPLICATION_STATUS_CHANGED;
        };
    }

    private String getNotificationTitle(ApplicationStatus status) {
        return switch (status) {
            case IN_REVIEW -> "Application Under Review";
            case MEETING_SCHEDULED -> "Interview Scheduled";
            case APPROVED -> "Application Approved";
            case REJECTED -> "Application Rejected";
            default -> "Application Status Updated";
        };
    }

    private String buildNotificationMessage(Application application, ApplicationStatus status) {
        String jobTitle = application.getJobOffer().getTitle();
        return switch (status) {
            case IN_REVIEW -> "Your application for \"" + jobTitle + "\" is now under review.";
            case MEETING_SCHEDULED -> "An interview has been scheduled for your application to \"" + jobTitle + "\".";
            case APPROVED -> "Congratulations! Your application for \"" + jobTitle + "\" has been approved.";
            case REJECTED -> "Your application for \"" + jobTitle + "\" has been rejected.";
            default -> "Your application for \"" + jobTitle + "\" status has been updated to " + status + ".";
        };
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
                .candidateResumeUrl(
                        candidateRepository.findById(application.getCandidate().getId()).get().getResumeUrl())
                .build();
    }
}
