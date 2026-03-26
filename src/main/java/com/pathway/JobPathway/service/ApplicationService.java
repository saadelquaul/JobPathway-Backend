package com.pathway.JobPathway.service;

import com.pathway.JobPathway.dto.ApplicationResponse;
import com.pathway.JobPathway.dto.ApplicationStatusUpdateRequest;
import com.pathway.JobPathway.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ApplicationService {
    ApplicationResponse applyForJob(User user, Long jobOfferId);
    ApplicationResponse getApplicationById(Long applicationId);
    List<ApplicationResponse> getMyApplications(User user);
    Page<ApplicationResponse> getMyApplications(User user, Pageable pageable);

    List<ApplicationResponse> getApplicationsByJobOffer(Long jobOfferId);
    Page<ApplicationResponse> getApplicationsByJobOffer(Long jobOfferId, Pageable pageable);
    Page<ApplicationResponse> getApplicationsByCandidateId(Long candidateId, Pageable pageable);
    ApplicationResponse updateApplicationStatus(Long applicationId, ApplicationStatusUpdateRequest request);
}
