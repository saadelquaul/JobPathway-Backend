package com.pathway.JobPathway.dto;

import com.pathway.JobPathway.entity.enums.SkillLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequiredSkillDTO {
    private Long id;

    @NotBlank(message = "Skill name is required")
    private String name;

    @NotNull(message = "Minimum level is required")
    private SkillLevel minimumLevel;
}
