package com.pathway.JobPathway.dto;

import com.pathway.JobPathway.entity.enums.JobType;
import com.pathway.JobPathway.entity.enums.WorkModel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobOfferRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private String requiredEducation;

    private String requiredExperience;

    private List<RequiredSkillDTO> requiredSkills;

    @NotNull(message = "Job type is required")
    private JobType type;

    @NotNull(message = "Work model is required")
    private WorkModel workModel;

    private String salaryRange;

    private String location;
}
