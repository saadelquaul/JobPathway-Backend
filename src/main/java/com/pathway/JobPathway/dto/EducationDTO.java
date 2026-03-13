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
public class EducationDTO {
    private Long id;

    @NotBlank(message = "School name is required")
    private String schoolName;

    private String degree;
    private String field;
    private LocalDate startDate;
    private LocalDate endDate;
}
