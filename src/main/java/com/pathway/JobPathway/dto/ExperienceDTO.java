package com.pathway.JobPathway.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExperienceDTO {
    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    private String companyName;
    private String location;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isCurrent;
    private String description;
}
