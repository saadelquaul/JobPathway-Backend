package com.pathway.JobPathway.dto;

import com.pathway.JobPathway.entity.enums.JobStatus;
import com.pathway.JobPathway.entity.enums.JobType;
import com.pathway.JobPathway.entity.enums.WorkModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobOfferResponse {
    private Long id;
    private String title;
    private String description;
    private String requiredEducation;
    private String requiredExperience;
    private List<RequiredSkillDTO> requiredSkills;
    private JobType type;
    private WorkModel workModel;
    private String salaryRange;
    private JobStatus status;
    private String location;
    private LocalDateTime createdAt;
    private String adminEmail;
}
