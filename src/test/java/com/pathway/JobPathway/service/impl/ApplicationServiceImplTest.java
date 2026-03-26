package com.pathway.JobPathway.service.impl;

import com.pathway.JobPathway.dto.ApplicationResponse;
import com.pathway.JobPathway.dto.ApplicationStatusUpdateRequest;
import com.pathway.JobPathway.entity.Application;
import com.pathway.JobPathway.entity.Candidate;
import com.pathway.JobPathway.entity.JobOffer;
import com.pathway.JobPathway.entity.User;
import com.pathway.JobPathway.entity.enums.ApplicationStatus;
import com.pathway.JobPathway.entity.enums.JobStatus;
import com.pathway.JobPathway.entity.enums.JobType;
import com.pathway.JobPathway.entity.enums.Role;
import com.pathway.JobPathway.entity.enums.WorkModel;
import com.pathway.JobPathway.exception.BadRequestException;
import com.pathway.JobPathway.exception.DuplicateResourceException;
import com.pathway.JobPathway.exception.ResourceNotFoundException;
import com.pathway.JobPathway.repository.ApplicationRepository;
import com.pathway.JobPathway.repository.CandidateRepository;
import com.pathway.JobPathway.repository.JobOfferRepository;
import com.pathway.JobPathway.service.NotificationService;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceImplTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private CandidateRepository candidateRepository;

    @Mock
    private JobOfferRepository jobOfferRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ApplicationServiceImpl applicationService;

    private User candidateUser;
    private User adminUser;
    private Candidate candidate;
    private JobOffer jobOffer;
    private Application application;

    @BeforeEach
    void setUp() {
        adminUser = User.builder()
                .id(1L)
                .email("admin@test.com")
                .name("Admin")
                .password("password")
                .role(Role.ADMIN)
                .build();

        candidateUser = User.builder()
                .id(2L)
                .email("candidate@test.com")
                .name("John Doe")
                .password("password")
                .role(Role.CANDIDATE)
                .build();

        candidate = Candidate.builder()
                .id(1L)
                .user(candidateUser)
                .resumeUrl("http://resume.pdf")
                .build();

        jobOffer = JobOffer.builder()
                .id(1L)
                .admin(adminUser)
                .title("Software Engineer")
                .description("Build great software")
                .type(JobType.FULL_TIME)
                .workModel(WorkModel.REMOTE)
                .status(JobStatus.OPEN)
                .requiredSkills(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .build();

        application = Application.builder()
                .id(1L)
                .candidate(candidate)
                .jobOffer(jobOffer)
                .status(ApplicationStatus.PENDING)
                .appliedAt(LocalDateTime.now())
                .build();
    }

    // ============================================================
    // applyForJob
    // ============================================================
    @Nested
    @DisplayName("applyForJob")
    class ApplyForJob {

        @Test
        @DisplayName("should create application successfully")
        void shouldCreateApplicationSuccessfully() {
            when(candidateRepository.findByUser(candidateUser)).thenReturn(Optional.of(candidate));
            when(jobOfferRepository.findById(1L)).thenReturn(Optional.of(jobOffer));
            when(applicationRepository.existsByCandidateIdAndJobOfferId(1L, 1L)).thenReturn(false);
            when(applicationRepository.save(any(Application.class))).thenReturn(application);
            when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));

            ApplicationResponse response = applicationService.applyForJob(candidateUser, 1L);

            assertThat(response).isNotNull();
            assertThat(response.getJobOfferId()).isEqualTo(1L);
            assertThat(response.getCandidateId()).isEqualTo(1L);
            assertThat(response.getStatus()).isEqualTo(ApplicationStatus.PENDING);
            verify(applicationRepository).save(any(Application.class));
            verify(notificationService).createNotification(eq(adminUser), any(), anyString(), anyString(), any(), anyString());
        }

        @Test
        @DisplayName("should throw when candidate profile not found")
        void shouldThrowWhenCandidateNotFound() {
            when(candidateRepository.findByUser(candidateUser)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> applicationService.applyForJob(candidateUser, 1L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Candidate profile not found");
        }

        @Test
        @DisplayName("should throw when job offer not found")
        void shouldThrowWhenJobOfferNotFound() {
            when(candidateRepository.findByUser(candidateUser)).thenReturn(Optional.of(candidate));
            when(jobOfferRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> applicationService.applyForJob(candidateUser, 99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Job offer not found");
        }

        @Test
        @DisplayName("should throw when job offer is closed")
        void shouldThrowWhenJobOfferClosed() {
            jobOffer.setStatus(JobStatus.CLOSED);
            when(candidateRepository.findByUser(candidateUser)).thenReturn(Optional.of(candidate));
            when(jobOfferRepository.findById(1L)).thenReturn(Optional.of(jobOffer));

            assertThatThrownBy(() -> applicationService.applyForJob(candidateUser, 1L))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("Cannot apply to a closed job offer");
        }

        @Test
        @DisplayName("should throw when duplicate application exists")
        void shouldThrowWhenDuplicateApplication() {
            when(candidateRepository.findByUser(candidateUser)).thenReturn(Optional.of(candidate));
            when(jobOfferRepository.findById(1L)).thenReturn(Optional.of(jobOffer));
            when(applicationRepository.existsByCandidateIdAndJobOfferId(1L, 1L)).thenReturn(true);

            assertThatThrownBy(() -> applicationService.applyForJob(candidateUser, 1L))
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessage("You have already applied to this job offer");
        }
    }

    // ============================================================
    // getApplicationById
    // ============================================================
    @Nested
    @DisplayName("getApplicationById")
    class GetApplicationById {

        @Test
        @DisplayName("should return application when found")
        void shouldReturnApplicationWhenFound() {
            when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
            when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));

            ApplicationResponse response = applicationService.getApplicationById(1L);

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getJobOfferTitle()).isEqualTo("Software Engineer");
        }

        @Test
        @DisplayName("should throw when application not found")
        void shouldThrowWhenApplicationNotFound() {
            when(applicationRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> applicationService.getApplicationById(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Application not found");
        }
    }

    // ============================================================
    // getMyApplications
    // ============================================================
    @Nested
    @DisplayName("getMyApplications")
    class GetMyApplications {

        @Test
        @DisplayName("should return list of applications for current user")
        void shouldReturnListOfApplications() {
            when(candidateRepository.findByUser(candidateUser)).thenReturn(Optional.of(candidate));
            when(applicationRepository.findByCandidateId(1L)).thenReturn(List.of(application));
            when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));

            List<ApplicationResponse> responses = applicationService.getMyApplications(candidateUser);

            assertThat(responses).hasSize(1);
            assertThat(responses.get(0).getCandidateName()).isEqualTo("John Doe");
        }

        @Test
        @DisplayName("should return paginated applications for current user")
        void shouldReturnPaginatedApplications() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Application> page = new PageImpl<>(List.of(application), pageable, 1);

            when(candidateRepository.findByUser(candidateUser)).thenReturn(Optional.of(candidate));
            when(applicationRepository.findByCandidateId(1L, pageable)).thenReturn(page);
            when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));

            Page<ApplicationResponse> responsePage = applicationService.getMyApplications(candidateUser, pageable);

            assertThat(responsePage.getTotalElements()).isEqualTo(1);
            assertThat(responsePage.getContent().get(0).getStatus()).isEqualTo(ApplicationStatus.PENDING);
        }

        @Test
        @DisplayName("should throw when candidate profile not found")
        void shouldThrowWhenCandidateNotFound() {
            when(candidateRepository.findByUser(candidateUser)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> applicationService.getMyApplications(candidateUser))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Candidate profile not found");
        }
    }

    // ============================================================
    // getApplicationsByJobOffer
    // ============================================================
    @Nested
    @DisplayName("getApplicationsByJobOffer")
    class GetApplicationsByJobOffer {

        @Test
        @DisplayName("should return list of applications for a job offer")
        void shouldReturnListOfApplications() {
            when(jobOfferRepository.findById(1L)).thenReturn(Optional.of(jobOffer));
            when(applicationRepository.findByJobOfferId(1L)).thenReturn(List.of(application));
            when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));

            List<ApplicationResponse> responses = applicationService.getApplicationsByJobOffer(1L);

            assertThat(responses).hasSize(1);
        }

        @Test
        @DisplayName("should return paginated applications for a job offer")
        void shouldReturnPaginatedApplications() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Application> page = new PageImpl<>(List.of(application), pageable, 1);

            when(jobOfferRepository.findById(1L)).thenReturn(Optional.of(jobOffer));
            when(applicationRepository.findByJobOfferId(1L, pageable)).thenReturn(page);
            when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));

            Page<ApplicationResponse> responsePage = applicationService.getApplicationsByJobOffer(1L, pageable);

            assertThat(responsePage.getTotalElements()).isEqualTo(1);
        }

        @Test
        @DisplayName("should throw when job offer not found (list)")
        void shouldThrowWhenJobOfferNotFoundList() {
            when(jobOfferRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> applicationService.getApplicationsByJobOffer(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Job offer not found");
        }

        @Test
        @DisplayName("should throw when job offer not found (paginated)")
        void shouldThrowWhenJobOfferNotFoundPage() {
            when(jobOfferRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> applicationService.getApplicationsByJobOffer(99L, PageRequest.of(0, 10)))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Job offer not found");
        }
    }

    // ============================================================
    // updateApplicationStatus
    // ============================================================
    @Nested
    @DisplayName("updateApplicationStatus")
    class UpdateApplicationStatus {

        @Test
        @DisplayName("should update status to IN_REVIEW")
        void shouldUpdateStatusToInReview() {
            ApplicationStatusUpdateRequest request = ApplicationStatusUpdateRequest.builder()
                    .status(ApplicationStatus.IN_REVIEW)
                    .build();

            when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
            when(applicationRepository.save(any(Application.class))).thenReturn(application);
            when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));

            ApplicationResponse response = applicationService.updateApplicationStatus(1L, request);

            assertThat(response).isNotNull();
            verify(applicationRepository).save(any(Application.class));
            verify(notificationService).createNotification(eq(candidateUser), any(), anyString(), anyString(), any(), anyString());
        }

        @Test
        @DisplayName("should update status to MEETING_SCHEDULED with meeting date")
        void shouldUpdateStatusToMeetingScheduledWithDate() {
            LocalDateTime meetingDate = LocalDateTime.now().plusDays(7);
            ApplicationStatusUpdateRequest request = ApplicationStatusUpdateRequest.builder()
                    .status(ApplicationStatus.MEETING_SCHEDULED)
                    .meetingDate(meetingDate)
                    .build();

            when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
            when(applicationRepository.save(any(Application.class))).thenReturn(application);
            when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));

            ApplicationResponse response = applicationService.updateApplicationStatus(1L, request);

            assertThat(response).isNotNull();
            verify(applicationRepository).save(any(Application.class));
        }

        @Test
        @DisplayName("should throw when MEETING_SCHEDULED without meeting date")
        void shouldThrowWhenMeetingScheduledWithoutDate() {
            ApplicationStatusUpdateRequest request = ApplicationStatusUpdateRequest.builder()
                    .status(ApplicationStatus.MEETING_SCHEDULED)
                    .meetingDate(null)
                    .build();

            when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));

            assertThatThrownBy(() -> applicationService.updateApplicationStatus(1L, request))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("Meeting date is required when status is MEETING_SCHEDULED");
        }

        @Test
        @DisplayName("should throw when application not found")
        void shouldThrowWhenApplicationNotFound() {
            ApplicationStatusUpdateRequest request = ApplicationStatusUpdateRequest.builder()
                    .status(ApplicationStatus.APPROVED)
                    .build();

            when(applicationRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> applicationService.updateApplicationStatus(99L, request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Application not found");
        }
    }
}
