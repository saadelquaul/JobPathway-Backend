# JobPathway Backend

**JobPathway** is a modern job portal REST API built with Spring Boot 3 and Java 17. It serves as the backend engine for a recruitment platform, connecting Candidates with system Administrators who manage job postings and applications.

## 🚀 Technologies Used
- **Language:** Java 17
- **Framework:** Spring Boot 3.x
- **Security:** Spring Security & JWT (JSON Web Tokens)
- **Persistence:** Spring Data JPA / Hibernate
- **Database:** H2 (In-Memory for Dev) / PostgreSQL (Production ready)
- **Build Tool:** Maven
- **Validation:** Spring Boot Starter Validation

## ✨ Key Features
- **Authentication & Authorization**: Secure JWT-based registration and login system with Role-Based Access Control (`ADMIN`, `CANDIDATE`).
- **Candidate Profiles**: Candidates can build comprehensive profiles including their educational background, work experiences, and a detailed list of skills with proficiency levels.
- **Job Offer Management**: Admins can create, update, and manage job listings with required skills, experience, and remote work models.
- **Application Tracking**: Candidates can apply to open job offers, and Admins can update the application status (e.g., `PENDING`, `IN_REVIEW`, `ACCEPTED`, `REJECTED`) and schedule meeting/interview dates.

## 📂 Project Structure Overview
- `config/` - Core configuration classes like `ApplicationConfig` setting up Beans for Security and Database seeding.
- `security/` - JWT filters, `SecurityConfig` for route protection, and CORS settings.
- `controller/` - REST API endpoints exposed to clients.
- `service/` - Core business logic interfacing between controllers and repositories.
- `repository/` - Spring Data JPA interfaces for database operations.
- `entity/` - Database models (`User`, `JobOffer`, `Application`, `CandidateSkill`, etc.).
- `dto/` - Data Transfer Objects used for request/response payloads and validation.

## 🛠️ How to Run Locally

### Prerequisites
- JDK 17+ installed.
- Maven installed (or use the provided `mvnw` wrapper).

### Steps
1. **Clone the repository.**
2. **Navigate to the project root:**
   ```bash
   cd JobPathway-Backend
   ```
3. **Run the application using the Maven wrapper:**
   ```bash
   ./mvnw spring-boot:run
   ```
   *The server will start on `http://localhost:8080`.*

### Default Admin Account
When the application starts, a default admin account is seeded into the database:
- **Email:** `admin@jobpathway.com`
- **Password:** `admin123`

## 🧪 Testing the API
A complete **Postman Collection** is included at the root of the project: 
`JobPathway_Postman_Collection.json`.

1. Open Postman.
2. Click **Import** and select the `JobPathway_Postman_Collection.json` file.
3. The collection is organized by folders (`Auth`, `Admin`, `Candidate Profile`, `Job Offers`, `Applications`).
4. **Important Settings:** The collection uses Collection Variables (`base_url`, `admin_token`, `candidate_token`). After logging in as an admin or candidate, copy the JWT from the response and paste it into the respective Collection Variable to authenticate subsequent requests.

## 📜 API Documentation highlights
*Full request/response schemas can be found in the Postman Collection.*

- `POST /api/auth/register` - Create a new Candidate.
- `POST /api/auth/login` - Authenticate and receive a JWT.
- `GET /api/job-offers` - Public endpoint to view all job offers.
- `POST /api/candidate/profile` - Update candidate profile details.
- `POST /api/applications/apply/{jobOfferId}` - Apply for a specific job (Candidate only).
- `PUT /api/applications/{id}/status` - Update an application's status (Admin only).
