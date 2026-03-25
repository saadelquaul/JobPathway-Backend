package com.pathway.JobPathway.service.impl;

import com.pathway.JobPathway.dto.JobOfferRequest;
import com.pathway.JobPathway.dto.JobOfferResponse;
import com.pathway.JobPathway.dto.JobOfferStatusRequest;
import com.pathway.JobPathway.dto.RequiredSkillDTO;
import com.pathway.JobPathway.entity.JobOffer;
import com.pathway.JobPathway.entity.RequiredSkill;
import com.pathway.JobPathway.entity.User;
import com.pathway.JobPathway.entity.enums.JobStatus;
import com.pathway.JobPathway.entity.enums.JobType;
import com.pathway.JobPathway.entity.enums.Role;
import com.pathway.JobPathway.entity.enums.SkillLevel;
import com.pathway.JobPathway.entity.enums.WorkModel;
import com.pathway.JobPathway.exception.ResourceNotFoundException;
import com.pathway.JobPathway.repository.JobOfferRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobOfferServiceImplTest {

    @Mock
    private JobOfferRepository jobOfferRepository;

    @InjectMocks
    private JobOfferServiceImpl jobOfferService;

    private User adminUser;
    private JobOffer jobOffer;
    private JobOfferRequest jobOfferRequest;

    @BeforeEach
    void setUp() {
        adminUser = User.builder()
                .id(1L)
                .email("admin@test.com")
                .name("Admin User")
                .password("password")
                .role(Role.ADMIN)
                .build();

        jobOffer = JobOffer.builder()
                .id(1L)
                .admin(adminUser)
                .title("Software Engineer")
                .description("Build great software")
                .requiredEducation("BS in Computer Science")
                .requiredExperience("3+ years")
                .type(JobType.FULL_TIME)
                .workModel(WorkModel.REMOTE)
                .salaryRange("$80,000 - $120,000")
                .status(JobStatus.OPEN)
                .location("New York")
                .requiredSkills(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .build();

        jobOfferRequest = JobOfferRequest.builder()
                .title("Software Engineer")
                .description("Build great software")
                .requiredEducation("BS in Computer Science")
                .requiredExperience("3+ years")
                .type(JobType.FULL_TIME)
                .workModel(WorkModel.REMOTE)
                .salaryRange("$80,000 - $120,000")
                .location("New York")
                .build();
    }

    // ============================================================
    // createJobOffer
    // ============================================================
    @Nested
    @DisplayName("createJobOffer")
    class CreateJobOffer {

        @Test
        @DisplayName("should create job offer with required skills")
        void shouldCreateJobOfferWithSkills() {
            List<RequiredSkillDTO> skills = List.of(
                    RequiredSkillDTO.builder().name("Java").minimumLevel(SkillLevel.ADVANCED).build(),
                    RequiredSkillDTO.builder().name("Spring Boot").minimumLevel(SkillLevel.INTERMEDIATE).build()
            );
            jobOfferRequest.setRequiredSkills(skills);

            RequiredSkill skill1 = RequiredSkill.builder().id(1L).name("Java").minimumLevel(SkillLevel.ADVANCED).jobOffer(jobOffer).build();
            RequiredSkill skill2 = RequiredSkill.builder().id(2L).name("Spring Boot").minimumLevel(SkillLevel.INTERMEDIATE).jobOffer(jobOffer).build();
            jobOffer.setRequiredSkills(new ArrayList<>(List.of(skill1, skill2)));

            when(jobOfferRepository.save(any(JobOffer.class))).thenReturn(jobOffer);

            JobOfferResponse response = jobOfferService.createJobOffer(adminUser, jobOfferRequest);

            assertThat(response).isNotNull();
            assertThat(response.getTitle()).isEqualTo("Software Engineer");
            assertThat(response.getRequiredSkills()).hasSize(2);
            assertThat(response.getAdminEmail()).isEqualTo("admin@test.com");
            assertThat(response.getStatus()).isEqualTo(JobStatus.OPEN);
            verify(jobOfferRepository).save(any(JobOffer.class));
        }

        @Test
        @DisplayName("should create job offer without required skills")
        void shouldCreateJobOfferWithoutSkills() {
            jobOfferRequest.setRequiredSkills(null);

            when(jobOfferRepository.save(any(JobOffer.class))).thenReturn(jobOffer);

            JobOfferResponse response = jobOfferService.createJobOffer(adminUser, jobOfferRequest);

            assertThat(response).isNotNull();
            assertThat(response.getTitle()).isEqualTo("Software Engineer");
            assertThat(response.getRequiredSkills()).isEmpty();
            verify(jobOfferRepository).save(any(JobOffer.class));
        }
    }

    // ============================================================
    // getJobOfferById
    // ============================================================
    @Nested
    @DisplayName("getJobOfferById")
    class GetJobOfferById {

        @Test
        @DisplayName("should return job offer when found")
        void shouldReturnJobOfferWhenFound() {
            when(jobOfferRepository.findById(1L)).thenReturn(Optional.of(jobOffer));

            JobOfferResponse response = jobOfferService.getJobOfferById(1L);

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getTitle()).isEqualTo("Software Engineer");
            assertThat(response.getType()).isEqualTo(JobType.FULL_TIME);
            assertThat(response.getWorkModel()).isEqualTo(WorkModel.REMOTE);
        }

        @Test
        @DisplayName("should throw when job offer not found")
        void shouldThrowWhenJobOfferNotFound() {
            when(jobOfferRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> jobOfferService.getJobOfferById(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Job offer not found");
        }
    }

    // ============================================================
    // getAllJobOffers
    // ============================================================
    @Nested
    @DisplayName("getAllJobOffers")
    class GetAllJobOffers {

        @Test
        @DisplayName("should return all job offers as list")
        void shouldReturnAllJobOffersAsList() {
            when(jobOfferRepository.findAll()).thenReturn(List.of(jobOffer));

            List<JobOfferResponse> responses = jobOfferService.getAllJobOffers();

            assertThat(responses).hasSize(1);
            assertThat(responses.get(0).getTitle()).isEqualTo("Software Engineer");
        }

        @Test
        @DisplayName("should return all job offers as page")
        void shouldReturnAllJobOffersAsPage() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<JobOffer> page = new PageImpl<>(List.of(jobOffer), pageable, 1);

            when(jobOfferRepository.findAll(pageable)).thenReturn(page);

            Page<JobOfferResponse> responsePage = jobOfferService.getAllJobOffers(pageable);

            assertThat(responsePage.getTotalElements()).isEqualTo(1);
            assertThat(responsePage.getContent().get(0).getTitle()).isEqualTo("Software Engineer");
        }
    }

    // ============================================================
    // getOpenJobOffers
    // ============================================================
    @Nested
    @DisplayName("getOpenJobOffers")
    class GetOpenJobOffers {

        @Test
        @DisplayName("should return open job offers as list")
        void shouldReturnOpenJobOffersAsList() {
            when(jobOfferRepository.findByStatus(JobStatus.OPEN)).thenReturn(List.of(jobOffer));

            List<JobOfferResponse> responses = jobOfferService.getOpenJobOffers();

            assertThat(responses).hasSize(1);
            assertThat(responses.get(0).getStatus()).isEqualTo(JobStatus.OPEN);
        }

        @Test
        @DisplayName("should return open job offers as page")
        void shouldReturnOpenJobOffersAsPage() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<JobOffer> page = new PageImpl<>(List.of(jobOffer), pageable, 1);

            when(jobOfferRepository.findByStatus(JobStatus.OPEN, pageable)).thenReturn(page);

            Page<JobOfferResponse> responsePage = jobOfferService.getOpenJobOffers(pageable);

            assertThat(responsePage.getTotalElements()).isEqualTo(1);
        }
    }

    // ============================================================
    // updateJobOffer
    // ============================================================
    @Nested
    @DisplayName("updateJobOffer")
    class UpdateJobOffer {

        @Test
        @DisplayName("should update job offer successfully")
        void shouldUpdateJobOfferSuccessfully() {
            JobOfferRequest updateRequest = JobOfferRequest.builder()
                    .title("Senior Software Engineer")
                    .description("Lead a team")
                    .requiredEducation("MS in Computer Science")
                    .requiredExperience("5+ years")
                    .type(JobType.FULL_TIME)
                    .workModel(WorkModel.HYBRID)
                    .salaryRange("$120,000 - $160,000")
                    .location("San Francisco")
                    .requiredSkills(List.of(
                            RequiredSkillDTO.builder().name("Java").minimumLevel(SkillLevel.EXPERT).build()
                    ))
                    .build();

            when(jobOfferRepository.findById(1L)).thenReturn(Optional.of(jobOffer));
            when(jobOfferRepository.save(any(JobOffer.class))).thenReturn(jobOffer);

            JobOfferResponse response = jobOfferService.updateJobOffer(1L, updateRequest);

            assertThat(response).isNotNull();
            verify(jobOfferRepository).save(any(JobOffer.class));
        }

        @Test
        @DisplayName("should throw when job offer not found")
        void shouldThrowWhenJobOfferNotFound() {
            when(jobOfferRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> jobOfferService.updateJobOffer(99L, jobOfferRequest))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Job offer not found");
        }
    }

    // ============================================================
    // updateJobOfferStatus
    // ============================================================
    @Nested
    @DisplayName("updateJobOfferStatus")
    class UpdateJobOfferStatus {

        @Test
        @DisplayName("should update job offer status successfully")
        void shouldUpdateStatusSuccessfully() {
            JobOfferStatusRequest statusRequest = new JobOfferStatusRequest();
            statusRequest.setStatus(JobStatus.CLOSED);

            when(jobOfferRepository.findById(1L)).thenReturn(Optional.of(jobOffer));
            when(jobOfferRepository.save(any(JobOffer.class))).thenReturn(jobOffer);

            JobOfferResponse response = jobOfferService.updateJobOfferStatus(1L, statusRequest);

            assertThat(response).isNotNull();
            verify(jobOfferRepository).save(any(JobOffer.class));
        }

        @Test
        @DisplayName("should throw when job offer not found")
        void shouldThrowWhenJobOfferNotFound() {
            JobOfferStatusRequest statusRequest = new JobOfferStatusRequest();
            statusRequest.setStatus(JobStatus.CLOSED);

            when(jobOfferRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> jobOfferService.updateJobOfferStatus(99L, statusRequest))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Job offer not found");
        }
    }

    // ============================================================
    // deleteJobOffer
    // ============================================================
    @Nested
    @DisplayName("deleteJobOffer")
    class DeleteJobOffer {

        @Test
        @DisplayName("should delete job offer successfully")
        void shouldDeleteJobOfferSuccessfully() {
            when(jobOfferRepository.findById(1L)).thenReturn(Optional.of(jobOffer));
            doNothing().when(jobOfferRepository).delete(jobOffer);

            jobOfferService.deleteJobOffer(1L);

            verify(jobOfferRepository).delete(jobOffer);
        }

        @Test
        @DisplayName("should throw when job offer not found")
        void shouldThrowWhenJobOfferNotFound() {
            when(jobOfferRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> jobOfferService.deleteJobOffer(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Job offer not found");
        }
    }
}
