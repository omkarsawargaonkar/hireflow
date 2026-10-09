# HireFlow

HireFlow is a full-stack recruitment and job application platform built using Java, Spring Boot, Spring Data JPA, Hibernate, MySQL, HTML, CSS, and JavaScript.

The platform connects candidates and recruiters through a structured recruitment workflow, from job posting and application submission to interview scheduling and final candidate selection.

---

## Features

### Candidate Features

- Candidate registration
- Browse available job postings
- View detailed job information
- Apply for jobs
- Duplicate application prevention
- Track application status
- View shortlisted applications
- View interview details
- View interview date and time
- View interview mode and meeting link
- View interview notes
- View final selection or rejection status

### Recruiter Features

- Recruiter registration
- Post new job openings
- View own job postings
- View applications for a specific job
- Shortlist candidates
- Reject candidates
- Schedule interviews
- Complete interviews
- Add interview notes
- Select candidates
- Reject candidates
- Close job postings

---

## Application Workflow

The candidate application follows a structured workflow:

```text
        APPLIED
           |
           v
      SHORTLISTED
           |
           v
 INTERVIEW_SCHEDULED
           |
           v
  Interview Completed
           |
       +---+---+
       |       |
       v       v
   SELECTED  REJECTED
```

### Application Statuses

- `APPLIED` - Candidate has submitted an application.
- `SHORTLISTED` - Recruiter has shortlisted the candidate.
- `INTERVIEW_SCHEDULED` - An interview has been scheduled.
- `SELECTED` - Candidate has been selected.
- `REJECTED` - Candidate has been rejected.

---

## Job Workflow

Recruiters can control whether a job is accepting applications.

```text
       New Job
          |
          v
         OPEN
          |
          | Recruiter closes job
          v
        CLOSED
```

### Job Statuses

- `OPEN` - Candidates can view and apply for the job.
- `CLOSED` - The job is no longer accepting new applications.

Closed jobs are no longer available for new applications.

---

## Interview Workflow

Interviews are associated with candidate applications.

```text
SHORTLISTED
     |
     v
Schedule Interview
     |
     v
  SCHEDULED
     |
     v
Complete Interview
     |
     v
 COMPLETED
```

Recruiters can also add interview notes during the interview process.

### Interview Statuses

- `SCHEDULED`
- `COMPLETED`
- `CANCELLED`

---

## Technology Stack

### Backend

- Java
- Spring Boot
- Spring Data JPA
- Hibernate
- REST APIs
- Maven
- Jakarta Bean Validation

### Database

- MySQL

### Frontend

- HTML5
- CSS3
- JavaScript
- Fetch API

### Development Tools

- IntelliJ IDEA
- Git
- GitHub
- MySQL

---

## Project Architecture

HireFlow follows a layered backend architecture:

```text
              Frontend
                  |
                  v
              REST API
                  |
                  v
              Controller
                  |
                  v
               Service
                  |
                  v
              Repository
                  |
                  v
               MySQL
```

### Controller Layer

The controller layer handles HTTP requests and exposes REST API endpoints.

Main controllers:

- `UserController`
- `JobController`
- `ApplicationController`
- `InterviewController`

### Service Layer

The service layer contains the application's business logic.

Main services:

- `UserService`
- `JobService`
- `ApplicationService`
- `InterviewService`

### Repository Layer

The repository layer uses Spring Data JPA to communicate with the MySQL database.

Main repositories include:

- `UserRepository`
- `JobRepository`
- `ApplicationRepository`
- `InterviewRepository`

### Entity Layer

The main entities are:

- `User`
- `Job`
- `Application`
- `Interview`

---

## Project Structure

```text
hireflow/
│
├── frontend/
│   ├── index.html
│   ├── jobs.html
│   ├── job-details.html
│   ├── register.html
│   ├── recruiter-register.html
│   ├── applications.html
│   ├── recruiter-dashboard.html
│   ├── recruiter-applications.html
│   ├── script.js
│   └── style.css
│
├── src/
│   └── main/
│       ├── java/
│       │   └── com/omkar/hireflow/
│       │       ├── config/
│       │       ├── controller/
│       │       ├── dto/
│       │       ├── entity/
│       │       ├── exception/
│       │       ├── repository/
│       │       └── service/
│       │
│       └── resources/
│           └── application.properties
│
├── pom.xml
├── mvnw
├── mvnw.cmd
├── .gitignore
├── .env.example
└── README.md
```

---

## Key Business Rules

HireFlow implements backend business rules to maintain a consistent recruitment workflow.

### Duplicate Application Prevention

A candidate cannot apply for the same job more than once.

```text
Candidate
    |
    v
Apply for Job
    |
    v
Already Applied?
   / \
 Yes  No
  |    |
  v    v
Reject  Create Application
```

### Closed Job Protection

Applications are accepted only when the job status is `OPEN`.

```text
Job Status
    |
    +---- OPEN ----> Application Allowed
    |
    +---- CLOSED --> Application Rejected
```

### Interview Scheduling Protection

An interview can only be scheduled for a shortlisted application.

```text
Application Status
        |
        v
   SHORTLISTED?
      /     \
    Yes      No
     |        |
     v        v
 Schedule   Reject
 Interview  Request
```

### Duplicate Interview Prevention

An application cannot have multiple interview records.

### Recruiter Authorization

A recruiter can close only their own job posting.

---

## REST API Overview

### User APIs

Register a user:

```text
POST /api/users
```

---

### Job APIs

Create a job:

```text
POST /api/jobs?recruiterId={recruiterId}
```

Get open jobs:

```text
GET /api/jobs
```

Get a specific job:

```text
GET /api/jobs/{id}
```

Get jobs posted by a recruiter:

```text
GET /api/jobs/recruiter/{recruiterId}
```

Close a job:

```text
PATCH /api/jobs/{id}/close?recruiterId={recruiterId}
```

---

### Application APIs

Apply for a job:

```text
POST /api/applications?candidateId={candidateId}&jobId={jobId}
```

Get applications of a candidate:

```text
GET /api/applications/candidate/{candidateId}
```

Get applications for a job:

```text
GET /api/applications/job/{jobId}
```

Update application status:

```text
PATCH /api/applications/{id}/status?status={status}
```

---

### Interview APIs

Schedule an interview:

```text
POST /api/interviews
```

Get interview by ID:

```text
GET /api/interviews/{id}
```

Get interview by application:

```text
GET /api/interviews/application/{applicationId}
```

Update interview status:

```text
PATCH /api/interviews/{id}/status?status={status}
```

Update interview notes:

```text
PATCH /api/interviews/{id}/notes
```

---

## Database

HireFlow uses MySQL as its relational database.

Database name:

```text
hire_flow
```

The application uses Spring Data JPA and Hibernate for database operations and object-relational mapping.

Hibernate can automatically update the database schema during development using:

```properties
spring.jpa.hibernate.ddl-auto=update
```

---

## Database Configuration

Database credentials are supplied through environment variables instead of being hard-coded.

The application uses:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/hire_flow
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
```

Set the following environment variables on your local machine:

```text
DB_USERNAME=root
DB_PASSWORD=your_mysql_password
```

Do not commit real database credentials to GitHub.

An example configuration is provided in:

```text
.env.example
```

---

## How to Run the Project

### Prerequisites

Make sure the following are installed:

- Java JDK
- Maven
- MySQL
- IntelliJ IDEA or another Java IDE
- Git

### 1. Clone the Repository

```bash
git clone YOUR_GITHUB_REPOSITORY_URL
```

Move into the project directory:

```bash
cd hireflow
```

### 2. Create the MySQL Database

Open MySQL and run:

```sql
CREATE DATABASE hire_flow;
```

### 3. Configure Environment Variables

Set:

```text
DB_USERNAME
DB_PASSWORD
```

according to your local MySQL configuration.

Example:

```text
DB_USERNAME=root
DB_PASSWORD=your_mysql_password
```

### 4. Open the Backend

Open the project in IntelliJ IDEA.

The Spring Boot backend is located in:

```text
src/main/java/com/omkar/hireflow/
```

### 5. Run the Application

Run the main Spring Boot application class:

```text
HireflowApplication.java
```

The backend will start on:

```text
http://localhost:8080
```

### 6. Run the Frontend

The frontend is located inside:

```text
frontend/
```

Open the HTML files using a local development server or your IDE's HTML runner.

The frontend communicates with the Spring Boot backend through REST APIs.

---

## Example Recruitment Flow

A typical HireFlow recruitment process works like this:

```text
Recruiter
    |
    v
Post Job
    |
    v
OPEN
    |
    v
Candidate Views Job
    |
    v
Candidate Applies
    |
    v
APPLIED
    |
    v
Recruiter Reviews Application
    |
    +----------------+
    |                |
    v                v
SHORTLISTED       REJECTED
    |
    v
Schedule Interview
    |
    v
INTERVIEW_SCHEDULED
    |
    v
Complete Interview
    |
    +----------------+
    |                |
    v                v
SELECTED         REJECTED
```

---

## Frontend Pages

### Candidate

- `index.html`
- `jobs.html`
- `job-details.html`
- `register.html`
- `applications.html`

### Recruiter

- `recruiter-register.html`
- `recruiter-dashboard.html`
- `recruiter-applications.html`

---

## Current Functional Modules

| Module | Status |
|---|---|
| Candidate Registration | Completed |
| Recruiter Registration | Completed |
| Job Posting | Completed |
| Job Listing | Completed |
| Job Details | Completed |
| Job Application | Completed |
| Duplicate Application Prevention | Completed |
| Application Status Management | Completed |
| Candidate Shortlisting | Completed |
| Interview Scheduling | Completed |
| Interview Completion | Completed |
| Interview Notes | Completed |
| Candidate Selection | Completed |
| Candidate Rejection | Completed |
| Job Closing | Completed |

---

## Future Enhancements

Possible future improvements include:

- Spring Security authentication and authorization
- Candidate and recruiter login
- Role-based access control
- Resume upload
- Job search and filtering
- Email notifications
- Recruiter analytics dashboard
- Pagination
- Advanced candidate search
- Cloud deployment
- Improved responsive design
- Automated testing
- API documentation with Swagger/OpenAPI

---

## Learning Objectives

This project was developed to gain practical experience with:

- Java backend development
- Spring Boot
- REST API development
- Spring Data JPA
- Hibernate
- MySQL database integration
- Entity relationships
- DTO-based API responses
- Backend validation
- Exception handling
- Business logic implementation
- Frontend-backend integration
- Git and GitHub

---

## Author

**Omkar Sawargaonkar**

Computer Engineering Graduate

---

## License

This project is intended for educational and portfolio purposes.