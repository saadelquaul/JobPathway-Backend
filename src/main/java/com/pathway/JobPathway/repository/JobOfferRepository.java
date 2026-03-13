package com.pathway.JobPathway.repository;

import com.pathway.JobPathway.entity.JobOffer;
import com.pathway.JobPathway.entity.enums.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobOfferRepository extends JpaRepository<JobOffer, Long> {
    List<JobOffer> findByStatus(JobStatus status);
    List<JobOffer> findByAdminId(Long adminId);
}
