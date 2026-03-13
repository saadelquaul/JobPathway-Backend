package com.pathway.JobPathway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateProfileUpdateRequest {
    private String name;
    private String resumeUrl;
    private String portfolioUrl;
    private String summary;
    private String profilePicture;
    private String password;
}
