package com.pathway.JobPathway.entity;

import com.pathway.JobPathway.entity.enums.JobStatus;
import com.pathway.JobPathway.entity.enums.JobType;
import com.pathway.JobPathway.entity.enums.WorkModel;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "job_offers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private User admin;

    @Column(nullable = false)
    private String title;

    @Column(length = 5000)
    private String description;

    @Column(length = 2000)
    private String requiredEducation;

    @Column(length = 2000)
    private String requiredExperience;

    @OneToMany(mappedBy = "jobOffer", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RequiredSkill> requiredSkills = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkModel workModel;

    private String salaryRange;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private JobStatus status = JobStatus.OPEN;

    private String location;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
