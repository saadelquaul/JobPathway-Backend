package com.pathway.JobPathway.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "experiences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Experience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    @Column(nullable = false)
    private String title;

    private String companyName;

    private String location;

    private LocalDate startDate;

    private LocalDate endDate;

    @Builder.Default
    private boolean isCurrent = false;

    @Column(length = 2000)
    private String description;
}
