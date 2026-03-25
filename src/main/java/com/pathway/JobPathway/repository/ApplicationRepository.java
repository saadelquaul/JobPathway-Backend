package com.pathway.JobPathway.repository;

import com.pathway.JobPathway.entity.Application;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByCandidateId(Long candidateId);
    Page<Application> findByCandidateId(Long candidateId, Pageable pageable);

    List<Application> findByJobOfferId(Long jobOfferId);
    Page<Application> findByJobOfferId(Long jobOfferId, Pageable pageable);

    Optional<Application> findByCandidateIdAndJobOfferId(Long candidateId, Long jobOfferId);
    boolean existsByCandidateIdAndJobOfferId(Long candidateId, Long jobOfferId);
}

