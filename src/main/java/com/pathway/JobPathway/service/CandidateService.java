package com.pathway.JobPathway.service;

import com.pathway.JobPathway.dto.*;
import com.pathway.JobPathway.entity.User;

import java.util.List;

public interface CandidateService {
    CandidateProfileResponse getProfile(User user);
    CandidateProfileResponse updateProfile(User user, CandidateProfileUpdateRequest request);
    EducationDTO addEducation(User user, EducationDTO dto);
    EducationDTO updateEducation(User user, Long educationId, EducationDTO dto);
    void deleteEducation(User user, Long educationId);
    ExperienceDTO addExperience(User user, ExperienceDTO dto);
    ExperienceDTO updateExperience(User user, Long experienceId, ExperienceDTO dto);
    void deleteExperience(User user, Long experienceId);
    CandidateSkillDTO addSkill(User user, CandidateSkillDTO dto);
    CandidateSkillDTO updateSkill(User user, Long skillId, CandidateSkillDTO dto);
    void deleteSkill(User user, Long skillId);
}
