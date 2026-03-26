# JobPathway Backend

**JobPathway** is a modern job recruitment platform REST API built with Spring Boot 4 and Java 17. It serves as the backend engine for a recruitment platform, connecting Candidates with system Administrators who manage job postings and applications.

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.1-green)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)
![Docker](https://img.shields.io/badge/Docker-Ready-blue)

## 📋 Table of Contents

- [Overview](#overview)
- [Use Case Diagram](#use-case-diagram)
- [Use Cases](#use-cases)
- [Architecture](#architecture)
- [Technologies Used](#technologies-used)
- [Project Structure](#project-structure)
- [Entity Relationship](#entity-relationship)
- [API Documentation](#api-documentation)
- [Getting Started](#getting-started)
- [Docker Deployment](#docker-deployment)
- [Testing](#testing)

---

## 🎯 Overview

JobPathway is a comprehensive recruitment platform that enables:
- **Candidates** to discover job opportunities, apply with resumes, and track their application status
- **Administrators** to manage job postings, review applications, schedule interviews, and communicate with candidates

### Key Capabilities
- JWT-based authentication with role-based access control
- Real-time notifications via WebSocket (STOMP over SockJS)
- File upload support for resumes (PDF) and profile pictures
- Comprehensive candidate profile management (education, experience, skills)
- Application workflow with meeting scheduling support

---

## 📊 Use Case Diagram

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                              JobPathway System                                   │
│                                                                                  │
│  ┌─────────────────────────────────────┐  ┌─────────────────────────────────┐  │
│  │         Guest Actions               │  │       Candidate Actions          │  │
│  │  ┌──────────────────────────────┐   │  │  ┌───────────────────────────┐   │  │
│  │  │ Register as Candidate        │   │  │  │ View/Edit Profile         │   │  │
│  │  └──────────────────────────────┘   │  │  └───────────────────────────┘   │  │
│  │  ┌──────────────────────────────┐   │  │  ┌───────────────────────────┐   │  │
│  │  │ Login                        │   │  │  │ Upload Resume             │   │  │
│  │  └──────────────────────────────┘   │  │  └───────────────────────────┘   │  │
│  │  ┌──────────────────────────────┐   │  │  ┌───────────────────────────┐   │  │
│  │  │ Browse Open Jobs             │   │  │  │ Upload Profile Picture    │   │  │
│  │  └──────────────────────────────┘   │  │  └───────────────────────────┘   │  │
│  └─────────────────────────────────────┘  │  ┌───────────────────────────┐   │  │
│                                           │  │ Manage Education          │   │  │
│  ┌─────────────────────────────────────┐  │  └───────────────────────────┘   │  │
│  │         Admin Actions               │  │  ┌───────────────────────────┐   │  │
│  │  ┌──────────────────────────────┐   │  │  │ Manage Experience         │   │  │
│  │  │ Manage Job Offers (CRUD)     │   │  │  └───────────────────────────┘   │  │
│  │  └──────────────────────────────┘   │  │  ┌───────────────────────────┐   │  │
│  │  ┌──────────────────────────────┐   │  │  │ Manage Skills             │   │  │
│  │  │ View All Applications        │   │  │  └───────────────────────────┘   │  │
│  │  └──────────────────────────────┘   │  │  ┌───────────────────────────┐   │  │
│  │  ┌──────────────────────────────┐   │  │  │ Apply to Jobs             │   │  │
│  │  │ Update Application Status    │   │  │  └───────────────────────────┘   │  │
│  │  └──────────────────────────────┘   │  │  ┌───────────────────────────┐   │  │
│  │  ┌──────────────────────────────┐   │  │  │ Track My Applications     │   │  │
│  │  │ Schedule Meetings            │   │  │  └───────────────────────────┘   │  │
│  │  └──────────────────────────────┘   │  │  ┌───────────────────────────┐   │  │
│  │  ┌──────────────────────────────┐   │  │  │ View Notifications        │   │  │
│  │  │ View Candidate Profiles      │   │  │  └───────────────────────────┘   │  │
│  │  └──────────────────────────────┘   │  └─────────────────────────────────┘  │
│  │  ┌──────────────────────────────┐   │                                       │
│  │  │ Manage Users                 │   │                                       │
│  │  └──────────────────────────────┘   │                                       │
│  │  ┌──────────────────────────────┐   │                                       │
│  │  │ View Notifications           │   │                                       │
│  │  └──────────────────────────────┘   │                                       │
│  └─────────────────────────────────────┘                                       │
└─────────────────────────────────────────────────────────────────────────────────┘

         ┌─────────┐         ┌───────────────┐         ┌─────────────┐
         │  Guest  │         │   Candidate   │         │    Admin    │
         └─────────┘         └───────────────┘         └─────────────┘
```

---

## 📝 Use Cases

### UC-01: User Registration
| Field | Description |
|-------|-------------|
| **Actor** | Guest |
| **Precondition** | User is not logged in |
| **Main Flow** | 1. Guest provides firstName, lastName, email, password<br>2. System validates email uniqueness<br>3. System creates Candidate profile<br>4. System returns JWT token |
| **Postcondition** | User is registered and authenticated |
| **Alternative** | Email already exists → Return 400 error |

### UC-02: User Login
| Field | Description |
|-------|-------------|
| **Actor** | Guest |
| **Precondition** | User has an account |
| **Main Flow** | 1. User provides email and password<br>2. System validates credentials<br>3. System returns JWT token with role |
| **Postcondition** | User is authenticated |
| **Alternative** | Invalid credentials → Return 401 error |

### UC-03: Manage Profile
| Field | Description |
|-------|-------------|
| **Actor** | Candidate |
| **Precondition** | User is authenticated as CANDIDATE |
| **Main Flow** | 1. Candidate updates profile info (phone, location, bio)<br>2. System validates and saves changes<br>3. System returns updated profile |
| **Postcondition** | Profile is updated |

### UC-04: Upload Resume
| Field | Description |
|-------|-------------|
| **Actor** | Candidate |
| **Precondition** | User is authenticated as CANDIDATE |
| **Main Flow** | 1. Candidate uploads PDF file (max 5MB)<br>2. System validates file type and size<br>3. System stores file and updates candidate record<br>4. System returns resume URL |
| **Postcondition** | Resume is stored and linked to profile |
| **Validation** | Only PDF files, max 5MB |

### UC-05: Upload Profile Picture
| Field | Description |
|-------|-------------|
| **Actor** | Candidate |
| **Precondition** | User is authenticated as CANDIDATE |
| **Main Flow** | 1. Candidate uploads image file (max 2MB)<br>2. System validates file type (JPG/PNG)<br>3. System stores file and updates user record<br>4. System returns image URL |
| **Postcondition** | Profile picture is updated |
| **Validation** | Only JPG/PNG files, max 2MB |

### UC-06: Manage Education
| Field | Description |
|-------|-------------|
| **Actor** | Candidate |
| **Precondition** | User is authenticated as CANDIDATE |
| **Main Flow** | 1. Candidate adds/updates/deletes education entry<br>2. System validates dates and required fields<br>3. System saves changes |
| **Fields** | institution, degree, fieldOfStudy, startDate, endDate |

### UC-07: Manage Experience
| Field | Description |
|-------|-------------|
| **Actor** | Candidate |
| **Precondition** | User is authenticated as CANDIDATE |
| **Main Flow** | 1. Candidate adds/updates/deletes experience entry<br>2. System validates dates and required fields<br>3. System saves changes |
| **Fields** | company, position, description, startDate, endDate, current |

### UC-08: Manage Skills
| Field | Description |
|-------|-------------|
| **Actor** | Candidate |
| **Precondition** | User is authenticated as CANDIDATE |
| **Main Flow** | 1. Candidate adds/updates/deletes skill<br>2. System validates skill name and level<br>3. System saves changes |
| **Fields** | skillName, level (BEGINNER, INTERMEDIATE, ADVANCED, EXPERT) |

### UC-09: Browse Job Offers
| Field | Description |
|-------|-------------|
| **Actor** | Guest, Candidate |
| **Precondition** | None |
| **Main Flow** | 1. User requests job listings<br>2. System returns paginated list of open jobs<br>3. User can view job details |
| **Postcondition** | Job listings displayed |

### UC-10: Apply to Job
| Field | Description |
|-------|-------------|
| **Actor** | Candidate |
| **Precondition** | User is authenticated, job is OPEN, no existing application |
| **Main Flow** | 1. Candidate selects job to apply<br>2. System creates application with PENDING status<br>3. System notifies admins via WebSocket<br>4. System returns application confirmation |
| **Postcondition** | Application created |
| **Alternative** | Already applied → Return 400 error |

### UC-11: Track Applications
| Field | Description |
|-------|-------------|
| **Actor** | Candidate |
| **Precondition** | User is authenticated as CANDIDATE |
| **Main Flow** | 1. Candidate requests their applications<br>2. System returns paginated list with status and meeting details |
| **Postcondition** | Applications displayed |

### UC-12: Create Job Offer
| Field | Description |
|-------|-------------|
| **Actor** | Admin |
| **Precondition** | User is authenticated as ADMIN |
| **Main Flow** | 1. Admin provides job details<br>2. System validates required fields<br>3. System creates job offer with OPEN status |
| **Fields** | title, description, location, minExperience, salaryRange, workModel, jobType, requiredSkills |
| **Postcondition** | Job offer created |

### UC-13: Update Application Status
| Field | Description |
|-------|-------------|
| **Actor** | Admin |
| **Precondition** | User is authenticated as ADMIN |
| **Main Flow** | 1. Admin selects application and new status<br>2. If MEETING_SCHEDULED, provide meetingDate and meetingLink<br>3. System updates status<br>4. System notifies candidate via WebSocket |
| **Status Values** | PENDING → IN_REVIEW → MEETING_SCHEDULED/REJECTED/APPROVED |
| **Postcondition** | Application status updated, candidate notified |

### UC-14: Schedule Meeting
| Field | Description |
|-------|-------------|
| **Actor** | Admin |
| **Precondition** | Application exists |
| **Main Flow** | 1. Admin sets status to MEETING_SCHEDULED<br>2. Admin provides meetingDate (LocalDateTime)<br>3. Admin provides meetingLink (Zoom, Google Meet, etc.)<br>4. System saves and notifies candidate |
| **Postcondition** | Meeting scheduled, candidate receives real-time notification |

### UC-15: View Notifications
| Field | Description |
|-------|-------------|
| **Actor** | Candidate, Admin |
| **Precondition** | User is authenticated |
| **Main Flow** | 1. User requests notifications<br>2. System returns paginated notifications<br>3. User can mark as read or delete |
| **Notification Types** | NEW_APPLICATION, APPLICATION_UNDER_REVIEW, MEETING_SCHEDULED, APPLICATION_REJECTED, APPLICATION_APPROVED |

---

## 🏗 Architecture

### System Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        Client Layer                             │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │              Angular Frontend (Port 4200)                │   │
│  │   - HTTP REST calls to /api/*                           │   │
│  │   - WebSocket connection to /ws (STOMP over SockJS)     │   │
│  └─────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Spring Boot Application                       │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                  Security Layer                          │   │
│  │   - JWT Authentication Filter                            │   │
│  │   - Role-based Authorization (ADMIN, CANDIDATE)         │   │
│  │   - CORS Configuration                                   │   │
│  └─────────────────────────────────────────────────────────┘   │
│                              │                                   │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                  Controller Layer                        │   │
│  │   - AuthController      - JobOfferController            │   │
│  │   - CandidateController - ApplicationController         │   │
│  │   - NotificationController - AdminUserController        │   │
│  └─────────────────────────────────────────────────────────┘   │
│                              │                                   │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                   Service Layer                          │   │
│  │   - Business Logic     - Validation                     │   │
│  │   - File Handling      - WebSocket Messaging            │   │
│  └─────────────────────────────────────────────────────────┘   │
│                              │                                   │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                 Repository Layer                         │   │
│  │   - Spring Data JPA    - Custom Queries                 │   │
│  └─────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                     Data Layer                                   │
│  ┌───────────────────┐    ┌─────────────────────────────────┐  │
│  │    PostgreSQL     │    │        File Storage             │  │
│  │   (Port 5432)     │    │  uploads/resumes/               │  │
│  │                   │    │  uploads/profiles/              │  │
│  └───────────────────┘    └─────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

### Request Flow

```
Client Request
      │
      ▼
┌─────────────────┐
│   CORS Filter   │
└─────────────────┘
      │
      ▼
┌─────────────────┐     ┌─────────────────┐
│  JWT Filter     │────▶│  JwtService     │
└─────────────────┘     │ (Token Verify)  │
      │                 └─────────────────┘
      ▼
┌─────────────────┐
│  Controller     │
└─────────────────┘
      │
      ▼
┌─────────────────┐     ┌─────────────────┐
│   Service       │────▶│  Repository     │
└─────────────────┘     └─────────────────┘
      │                         │
      │                         ▼
      │                 ┌─────────────────┐
      │                 │   Database      │
      │                 └─────────────────┘
      │
      ▼
┌─────────────────┐
│ WebSocket       │ (For real-time notifications)
│ SimpMessaging   │
└─────────────────┘
```

---

## 🚀 Technologies Used

| Category | Technology | Version | Purpose |
|----------|------------|---------|---------|
| **Language** | Java | 17 | Core programming language |
| **Framework** | Spring Boot | 4.0.1 | Application framework |
| **Security** | Spring Security | 6.x | Authentication & authorization |
| **JWT** | jjwt | 0.12.6 | Token generation & validation |
| **Persistence** | Spring Data JPA | - | Database abstraction |
| **ORM** | Hibernate | - | Object-relational mapping |
| **Database** | PostgreSQL | 16 | Production database |
| **Database (Dev)** | H2 | - | In-memory development database |
| **WebSocket** | Spring WebSocket | - | Real-time communication |
| **Validation** | Jakarta Validation | - | Input validation |
| **Build** | Maven | 3.x | Build automation |
| **Utility** | Lombok | - | Boilerplate reduction |
| **Container** | Docker | - | Containerization |

---

## 📁 Project Structure

```
src/main/java/com/pathway/JobPathway/
├── config/                          # Configuration classes
│   ├── ApplicationConfig.java       # Bean configurations
│   ├── DataInitializer.java         # Seed default admin
│   ├── WebConfig.java               # Static resource serving
│   ├── WebSocketConfig.java         # STOMP WebSocket config
│   └── WebSocketAuthConfig.java     # WebSocket authentication
│
├── controller/                      # REST API endpoints
│   ├── AuthController.java          # /api/auth/*
│   ├── JobOfferController.java      # /api/job-offers/*
│   ├── ApplicationController.java   # /api/applications/*
│   ├── CandidateController.java     # /api/candidate/*
│   ├── NotificationController.java  # /api/notifications/*
│   └── AdminUserController.java     # /api/admin/users/*
│
├── dto/                             # Data Transfer Objects
│   ├── AuthResponse.java            # Login/register response
│   ├── LoginRequest.java            # Login payload
│   ├── RegisterRequest.java         # Registration payload
│   ├── JobOfferRequest.java         # Create/update job
│   ├── JobOfferResponse.java        # Job listing response
│   ├── ApplicationResponse.java     # Application details
│   ├── ApplicationStatusUpdateRequest.java
│   ├── CandidateProfileResponse.java
│   ├── CandidateProfileUpdateRequest.java
│   ├── EducationDTO.java
│   ├── ExperienceDTO.java
│   ├── CandidateSkillDTO.java
│   ├── RequiredSkillDTO.java
│   └── NotificationResponse.java
│
├── entity/                          # JPA Entities
│   ├── User.java                    # Base user entity
│   ├── Candidate.java               # Candidate profile
│   ├── JobOffer.java                # Job posting
│   ├── Application.java             # Job application
│   ├── Education.java               # Education entry
│   ├── Experience.java              # Work experience
│   ├── CandidateSkill.java          # Candidate skill
│   ├── RequiredSkill.java           # Job required skill
│   ├── Notification.java            # User notification
│   └── enums/                       # Enumerations
│       ├── Role.java                # ADMIN, CANDIDATE
│       ├── ApplicationStatus.java   # PENDING, IN_REVIEW, etc.
│       ├── JobStatus.java           # OPEN, CLOSED
│       ├── JobType.java             # FULL_TIME, PART_TIME, etc.
│       ├── WorkModel.java           # ON_SITE, REMOTE, HYBRID
│       ├── SkillLevel.java          # BEGINNER to EXPERT
│       └── NotificationType.java    # Notification categories
│
├── exception/                       # Exception handling
│   └── GlobalExceptionHandler.java  # @ControllerAdvice
│
├── repository/                      # Data access layer
│   ├── UserRepository.java
│   ├── CandidateRepository.java
│   ├── JobOfferRepository.java
│   ├── ApplicationRepository.java
│   ├── EducationRepository.java
│   ├── ExperienceRepository.java
│   ├── CandidateSkillRepository.java
│   └── NotificationRepository.java
│
├── security/                        # Security configuration
│   ├── SecurityConfig.java          # Security filter chain
│   ├── JwtAuthenticationFilter.java # JWT validation filter
│   └── JwtService.java              # Token operations
│
├── service/                         # Business logic
│   ├── AuthService.java
│   ├── AuthServiceImpl.java
│   ├── JobOfferService.java
│   ├── JobOfferServiceImpl.java
│   ├── ApplicationService.java
│   ├── ApplicationServiceImpl.java
│   ├── CandidateService.java
│   ├── CandidateServiceImpl.java
│   └── NotificationService.java
│
└── JobPathwayApplication.java       # Main entry point
```

---

## 🔗 Entity Relationship

```
┌─────────────────┐         ┌─────────────────┐
│      User       │         │    Candidate    │
├─────────────────┤         ├─────────────────┤
│ id              │◄───────▶│ id              │
│ firstName       │   1:1   │ user_id (FK)    │
│ lastName        │         │ phone           │
│ email           │         │ location        │
│ password        │         │ bio             │
│ role (ENUM)     │         │ resumeUrl       │
│ profilePicture  │         └────────┬────────┘
└─────────────────┘                  │
                                     │ 1:N
         ┌───────────────────────────┼───────────────────────────┐
         │                           │                           │
         ▼                           ▼                           ▼
┌─────────────────┐         ┌─────────────────┐         ┌─────────────────┐
│   Education     │         │   Experience    │         │ CandidateSkill  │
├─────────────────┤         ├─────────────────┤         ├─────────────────┤
│ id              │         │ id              │         │ id              │
│ candidate_id    │         │ candidate_id    │         │ candidate_id    │
│ institution     │         │ company         │         │ skillName       │
│ degree          │         │ position        │         │ level (ENUM)    │
│ fieldOfStudy    │         │ description     │         └─────────────────┘
│ startDate       │         │ startDate       │
│ endDate         │         │ endDate         │
└─────────────────┘         │ current         │
                            └─────────────────┘

┌─────────────────┐                             ┌─────────────────┐
│    JobOffer     │                             │   Application   │
├─────────────────┤         N:1                 ├─────────────────┤
│ id              │◄────────────────────────────│ id              │
│ title           │                             │ job_offer_id    │
│ description     │         N:1                 │ candidate_id    │
│ location        │◄────────────────────────────│ status (ENUM)   │
│ minExperience   │         Candidate           │ appliedAt       │
│ salaryRange     │                             │ meetingDate     │
│ workModel       │                             │ meetingLink     │
│ jobType         │                             └─────────────────┘
│ status          │
│ createdBy_id    │
│ createdAt       │
└────────┬────────┘
         │ 1:N
         ▼
┌─────────────────┐
│  RequiredSkill  │
├─────────────────┤
│ id              │
│ job_offer_id    │
│ skillName       │
│ minimumLevel    │
└─────────────────┘

┌─────────────────┐
│  Notification   │
├─────────────────┤
│ id              │
│ user_id (FK)    │
│ type (ENUM)     │
│ title           │
│ message         │
│ read            │
│ createdAt       │
│ relatedEntityId │
└─────────────────┘
```

---

## 📜 API Documentation

### Base URL
```
http://localhost:8080/api
```

### Authentication
All endpoints except `/api/auth/*` and `GET /api/job-offers/*` require JWT authentication.

**Header Format:**
```
Authorization: Bearer <jwt_token>
```

---

### Authentication Endpoints

#### Register Candidate
```http
POST /api/auth/register
```
**Request Body:**
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "password": "password123"
}
```
**Response:** `201 Created`
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI...",
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "role": "CANDIDATE",
  "profilePicture": null
}
```

#### Login
```http
POST /api/auth/login
```
**Request Body:**
```json
{
  "email": "john@example.com",
  "password": "password123"
}
```
**Response:** `200 OK`
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI...",
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "role": "CANDIDATE",
  "profilePicture": "/profiles/uuid_image.png"
}
```

---

### Job Offer Endpoints

#### List All Job Offers (Paginated)
```http
GET /api/job-offers?page=0&size=10&sort=createdAt,desc
```
**Access:** Public

#### List Open Job Offers
```http
GET /api/job-offers/open?page=0&size=10
```
**Access:** Public

#### Get Job Offer by ID
```http
GET /api/job-offers/{id}
```
**Access:** Public

#### Create Job Offer
```http
POST /api/job-offers
```
**Access:** ADMIN only
**Request Body:**
```json
{
  "title": "Senior Java Developer",
  "description": "We are looking for...",
  "location": "New York, NY",
  "minExperience": 5,
  "salaryRange": "$120,000 - $150,000",
  "workModel": "HYBRID",
  "jobType": "FULL_TIME",
  "requiredSkills": [
    { "skillName": "Java", "minimumLevel": "ADVANCED" },
    { "skillName": "Spring Boot", "minimumLevel": "INTERMEDIATE" }
  ]
}
```
**Response:** `201 Created`

#### Update Job Offer
```http
PUT /api/job-offers/{id}
```
**Access:** ADMIN only

#### Update Job Offer Status
```http
PUT /api/job-offers/{id}/status
```
**Access:** ADMIN only
```json
{
  "status": "CLOSED"
}
```

#### Delete Job Offer
```http
DELETE /api/job-offers/{id}
```
**Access:** ADMIN only
**Response:** `204 No Content`

---

### Application Endpoints

#### Apply for Job
```http
POST /api/applications/apply/{jobOfferId}
```
**Access:** CANDIDATE only
**Response:** `201 Created`

#### Get My Applications
```http
GET /api/applications/my-applications?page=0&size=10
```
**Access:** CANDIDATE only

#### Get Applications for Job Offer
```http
GET /api/applications/job-offer/{jobOfferId}?page=0&size=10
```
**Access:** ADMIN only

#### Get Application by ID
```http
GET /api/applications/{id}
```
**Access:** Authenticated

#### Update Application Status
```http
PUT /api/applications/{id}/status
```
**Access:** ADMIN only
**Request Body:**
```json
{
  "status": "MEETING_SCHEDULED",
  "meetingDate": "2026-04-01T10:00:00",
  "meetingLink": "https://zoom.us/j/123456789"
}
```

**Status Values:**
| Status | Description |
|--------|-------------|
| `PENDING` | Initial state after application |
| `IN_REVIEW` | Admin is reviewing |
| `MEETING_SCHEDULED` | Interview scheduled (requires meetingDate & meetingLink) |
| `REJECTED` | Application rejected |
| `APPROVED` | Application accepted |

---

### Candidate Profile Endpoints

#### Get Profile
```http
GET /api/candidate/profile
```
**Access:** CANDIDATE only

#### Update Profile
```http
PUT /api/candidate/profile
```
**Access:** CANDIDATE only
```json
{
  "phone": "+1234567890",
  "location": "San Francisco, CA",
  "bio": "Experienced developer..."
}
```

#### Upload Resume
```http
POST /api/candidate/resume
Content-Type: multipart/form-data
```
**Access:** CANDIDATE only
**Parameter:** `file` (PDF, max 5MB)
**Response:**
```json
{
  "url": "/resumes/uuid_resume.pdf"
}
```

#### Upload Profile Picture
```http
POST /api/candidate/profile-picture
Content-Type: multipart/form-data
```
**Access:** CANDIDATE only
**Parameter:** `file` (JPG/PNG, max 2MB)
**Response:**
```json
{
  "url": "/profiles/uuid_image.png"
}
```

#### Add Education
```http
POST /api/candidate/education
```
```json
{
  "institution": "MIT",
  "degree": "Bachelor of Science",
  "fieldOfStudy": "Computer Science",
  "startDate": "2018-09-01",
  "endDate": "2022-06-01"
}
```

#### Update/Delete Education
```http
PUT /api/candidate/education/{id}
DELETE /api/candidate/education/{id}
```

#### Add Experience
```http
POST /api/candidate/experience
```
```json
{
  "company": "Google",
  "position": "Software Engineer",
  "description": "Worked on...",
  "startDate": "2022-07-01",
  "endDate": null,
  "current": true
}
```

#### Update/Delete Experience
```http
PUT /api/candidate/experience/{id}
DELETE /api/candidate/experience/{id}
```

#### Add Skill
```http
POST /api/candidate/skills
```
```json
{
  "skillName": "Java",
  "level": "EXPERT"
}
```

#### Update/Delete Skill
```http
PUT /api/candidate/skills/{id}
DELETE /api/candidate/skills/{id}
```

**Skill Levels:** `BEGINNER`, `INTERMEDIATE`, `ADVANCED`, `EXPERT`

---

### Admin User Management Endpoints

#### List All Candidates
```http
GET /api/admin/users?page=0&size=10
```
**Access:** ADMIN only

#### Get Candidate Profile
```http
GET /api/admin/users/{candidateId}/profile
```
**Access:** ADMIN only

#### Get Candidate Applications
```http
GET /api/admin/users/{candidateId}/applications?page=0&size=10
```
**Access:** ADMIN only

---

### Notification Endpoints

#### Get Notifications
```http
GET /api/notifications?page=0&size=10
```
**Access:** Authenticated

#### Get Unread Count
```http
GET /api/notifications/unread-count
```
**Response:**
```json
{
  "count": 5
}
```

#### Mark as Read
```http
PUT /api/notifications/{id}/read
```

#### Mark All as Read
```http
PUT /api/notifications/mark-all-read
```

#### Delete Notification
```http
DELETE /api/notifications/{id}
```

---

### WebSocket Endpoints

#### Connection
```
Endpoint: ws://localhost:8080/ws
Protocol: STOMP over SockJS
```

#### Subscribe to Notifications
```
Topic: /topic/notifications/{userId}
```

**Notification Message Format:**
```json
{
  "id": 1,
  "type": "MEETING_SCHEDULED",
  "title": "Interview Scheduled",
  "message": "Your interview for Senior Developer is scheduled for April 1st",
  "read": false,
  "createdAt": "2026-03-26T10:00:00",
  "relatedEntityId": 123
}
```

---

### Static File Endpoints

| Endpoint | Description |
|----------|-------------|
| `GET /resumes/{filename}` | Download resume (requires auth) |
| `GET /profiles/{filename}` | View profile picture (public) |

---

## 🛠️ Getting Started

### Prerequisites
- JDK 17+
- Maven 3.x (or use included wrapper)
- Docker & Docker Compose (for containerized deployment)

### Local Development (H2 Database)

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd JobPathway-Backend
   ```

2. **Run with Maven**
   ```bash
   ./mvnw spring-boot:run
   ```
   Server starts at `http://localhost:8080`

3. **H2 Console** (Development only)
   - URL: `http://localhost:8080/h2-console`
   - JDBC URL: `jdbc:h2:mem:testdb`

### Default Admin Account
```
Email: admin@jobpathway.com
Password: admin123
```

---

## 🐳 Docker Deployment

### Using Docker Compose (Recommended)

```bash
# Build and start all services
docker-compose up -d

# View logs
docker-compose logs -f app

# Stop services
docker-compose down
```

**Services:**
| Service | Container | Port |
|---------|-----------|------|
| Spring Boot App | jobpathway-app | 8080 |
| PostgreSQL | jobpathway-db | 5432 |

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_PROFILES_ACTIVE` | Active profile | `prod` |
| `DB_HOST` | Database host | `postgres` |
| `DB_PORT` | Database port | `5432` |
| `DB_NAME` | Database name | `jobpathway` |
| `DB_USERNAME` | Database user | `jobpathway` |
| `DB_PASSWORD` | Database password | `jobpathway` |

### Volumes
- `postgres_data` - PostgreSQL data persistence
- `app_uploads` - Uploaded files (resumes, profile pictures)

---

## 🧪 Testing

### Postman Collection
A complete Postman collection is included:
```
JobPathway_Postman_Collection.json
```

**Setup:**
1. Import collection into Postman
2. Set Collection Variables:
   - `base_url`: `http://localhost:8080`
   - `admin_token`: JWT from admin login
   - `candidate_token`: JWT from candidate login

### Running Unit Tests
```bash
./mvnw test
```

---

## 📝 License

This project is part of the JobPathway recruitment platform.

---

Built with ❤️ using Spring Boot 4
