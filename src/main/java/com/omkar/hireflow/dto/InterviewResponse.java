package com.omkar.hireflow.dto;

import com.omkar.hireflow.entity.InterviewStatus;

import java.time.LocalDateTime;

public class InterviewResponse {

    private Long id;
    private Long applicationId;
    private Long candidateId;
    private String candidateName;
    private Long jobId;
    private String jobTitle;
    private LocalDateTime scheduledAt;
    private String mode;
    private String meetingLink;
    private String location;
    private InterviewStatus status;
    private String notes;

    public InterviewResponse() {
    }

    public InterviewResponse(Long id,
                             Long applicationId,
                             Long candidateId,
                             String candidateName,
                             Long jobId,
                             String jobTitle,
                             LocalDateTime scheduledAt,
                             String mode,
                             String meetingLink,
                             String location,
                             InterviewStatus status,
                             String notes) {

        this.id = id;
        this.applicationId = applicationId;
        this.candidateId = candidateId;
        this.candidateName = candidateName;
        this.jobId = jobId;
        this.jobTitle = jobTitle;
        this.scheduledAt = scheduledAt;
        this.mode = mode;
        this.meetingLink = meetingLink;
        this.location = location;
        this.status = status;
        this.notes = notes;
    }

    public Long getId() {
        return id;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public Long getCandidateId() {
        return candidateId;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public Long getJobId() {
        return jobId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public String getMode() {
        return mode;
    }

    public String getMeetingLink() {
        return meetingLink;
    }

    public String getLocation() {
        return location;
    }

    public InterviewStatus getStatus() {
        return status;
    }

    public String getNotes() {
        return notes;
    }
}