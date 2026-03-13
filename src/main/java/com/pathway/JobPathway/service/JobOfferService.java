package com.pathway.JobPathway.service;

import com.pathway.JobPathway.dto.JobOfferRequest;
import com.pathway.JobPathway.dto.JobOfferResponse;
import com.pathway.JobPathway.entity.User;

import java.util.List;

public interface JobOfferService {
    JobOfferResponse createJobOffer(User admin, JobOfferRequest request);
    JobOfferResponse getJobOfferById(Long id);
    List<JobOfferResponse> getAllJobOffers();
    List<JobOfferResponse> getOpenJobOffers();
    JobOfferResponse updateJobOffer(Long id, JobOfferRequest request);
    void deleteJobOffer(Long id);
}
