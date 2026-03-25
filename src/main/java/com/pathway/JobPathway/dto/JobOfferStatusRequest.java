package com.pathway.JobPathway.dto;

import com.pathway.JobPathway.entity.enums.JobStatus;
import lombok.Data;

@Data
public class JobOfferStatusRequest {

    private JobStatus status;
}
