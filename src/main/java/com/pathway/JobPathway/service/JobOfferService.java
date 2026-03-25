package com.pathway.JobPathway.service;

import com.pathway.JobPathway.dto.JobOfferRequest;
import com.pathway.JobPathway.dto.JobOfferResponse;
import com.pathway.JobPathway.dto.JobOfferStatusRequest;
import com.pathway.JobPathway.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface JobOfferService {
    JobOfferResponse createJobOffer(User admin, JobOfferRequest request);

    JobOfferResponse getJobOfferById(Long id);

    List<JobOfferResponse> getAllJobOffers();

    Page<JobOfferResponse> getAllJobOffers(Pageable pageable);

    List<JobOfferResponse> getOpenJobOffers();

    Page<JobOfferResponse> getOpenJobOffers(Pageable pageable);

    JobOfferResponse updateJobOffer(Long id, JobOfferRequest request);

    void deleteJobOffer(Long id);

    JobOfferResponse updateJobOfferStatus(Long id, JobOfferStatusRequest request);
}
