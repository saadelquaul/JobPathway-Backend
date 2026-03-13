package com.pathway.JobPathway.service;

import com.pathway.JobPathway.dto.ApplicationResponse;
import com.pathway.JobPathway.dto.ApplicationStatusUpdateRequest;
import com.pathway.JobPathway.entity.User;

import java.util.List;

public interface ApplicationService {
    ApplicationResponse applyForJob(User user, Long jobOfferId);
    List<ApplicationResponse> getMyApplications(User user);
    List<ApplicationResponse> getApplicationsByJobOffer(Long jobOfferId);
    ApplicationResponse updateApplicationStatus(Long applicationId, ApplicationStatusUpdateRequest request);
}
