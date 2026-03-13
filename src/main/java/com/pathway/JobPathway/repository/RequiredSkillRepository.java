package com.pathway.JobPathway.repository;

import com.pathway.JobPathway.entity.RequiredSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequiredSkillRepository extends JpaRepository<RequiredSkill, Long> {
    List<RequiredSkill> findByJobOfferId(Long jobOfferId);
}
