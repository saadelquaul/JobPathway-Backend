package com.pathway.JobPathway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateProfileResponse {
    private Long id;
    private String email;
    private String name;
    private String profilePicture;
    private boolean isEnabled;
    private String resumeUrl;
    private String portfolioUrl;
    private String summary;
    private List<EducationDTO> educations;
    private List<ExperienceDTO> experiences;
    private List<CandidateSkillDTO> skills;
}
