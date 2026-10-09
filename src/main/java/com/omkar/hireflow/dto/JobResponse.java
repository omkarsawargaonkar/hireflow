package com.omkar.hireflow.dto;

import com.omkar.hireflow.entity.JobStatus;

import java.time.LocalDateTime;

public class JobResponse {

    private Long id;
    private String title;
    private String description;
    private String companyName;
    private String location;
    private String employmentType;
    private String salary;
    private String skills;
    private JobStatus status;
    private LocalDateTime createdAt;
    private Long recruiterId;

    public JobResponse() {
    }

    public JobResponse(Long id,
                       String title,
                       String description,
                       String companyName,
                       String location,
                       String employmentType,
                       String salary,
                       String skills,
                       JobStatus status,
                       LocalDateTime createdAt,
                       Long recruiterId) {

        this.id = id;
        this.title = title;
        this.description = description;
        this.companyName = companyName;
        this.location = location;
        this.employmentType = employmentType;
        this.salary = salary;
        this.skills = skills;
        this.status = status;
        this.createdAt = createdAt;
        this.recruiterId = recruiterId;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getLocation() {
        return location;
    }

    public String getEmploymentType() {
        return employmentType;
    }

    public String getSalary() {
        return salary;
    }

    public String getSkills() {
        return skills;
    }

    public JobStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Long getRecruiterId() {
        return recruiterId;
    }
}