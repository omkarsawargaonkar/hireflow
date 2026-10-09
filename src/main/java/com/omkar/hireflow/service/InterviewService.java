package com.omkar.hireflow.service;

import com.omkar.hireflow.dto.InterviewRequest;
import com.omkar.hireflow.dto.InterviewResponse;
import com.omkar.hireflow.entity.Application;
import com.omkar.hireflow.entity.ApplicationStatus;
import com.omkar.hireflow.entity.Interview;
import com.omkar.hireflow.entity.InterviewStatus;
import com.omkar.hireflow.entity.User;
import com.omkar.hireflow.repository.ApplicationRepository;
import com.omkar.hireflow.repository.InterviewRepository;
import org.springframework.stereotype.Service;
import com.omkar.hireflow.exception.BusinessException;
import com.omkar.hireflow.exception.ResourceNotFoundException;

@Service
public class InterviewService {

    private final InterviewRepository interviewRepository;
    private final ApplicationRepository applicationRepository;

    public InterviewService(InterviewRepository interviewRepository,
                            ApplicationRepository applicationRepository) {
        this.interviewRepository = interviewRepository;
        this.applicationRepository = applicationRepository;
    }

    public InterviewResponse scheduleInterview(InterviewRequest request, User recruiter) {

        Application application = applicationRepository.findById(request.getApplicationId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Application not found with id: " + request.getApplicationId()));

        if (!application.getJob().getRecruiter().getId().equals(recruiter.getId())) {
            throw new com.omkar.hireflow.exception.ForbiddenException("You can only schedule interviews for your own jobs");
        }

        if (interviewRepository.existsByApplication(application)) {
            throw new BusinessException(
                    "An interview is already scheduled for this application");
        }

        if (application.getStatus() != ApplicationStatus.SHORTLISTED) {
            throw new BusinessException(
                    "Interview can only be scheduled for a shortlisted application");
        }

        Interview interview = new Interview(
                application,
                request.getScheduledAt(),
                request.getMode(),
                request.getMeetingLink(),
                request.getLocation()
        );

        Interview savedInterview = interviewRepository.save(interview);

        application.setStatus(ApplicationStatus.INTERVIEW_SCHEDULED);
        applicationRepository.save(application);

        return mapToResponse(savedInterview);
    }

    public InterviewResponse getInterviewById(Long id, User user) {

        Interview interview = interviewRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                "Interview not found with id: " + id));

        ensureCanView(interview.getApplication(), user);
        return mapToResponse(interview);
    }

    public InterviewResponse getInterviewByApplicationId(Long applicationId, User user) {

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Application not found with id: " + applicationId));

        Interview interview = interviewRepository.findByApplication(application)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Interview not found for application id: " + applicationId));

        ensureCanView(application, user);
        return mapToResponse(interview);
    }

    public InterviewResponse updateInterviewStatus(Long id,
                                                   InterviewStatus newStatus,
                                                   User recruiter) {

        Interview interview = interviewRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Interview not found with id: " + id));

        ensureRecruiterOwnsInterview(interview, recruiter);
        interview.setStatus(newStatus);

        Interview updatedInterview = interviewRepository.save(interview);

        return mapToResponse(updatedInterview);
    }

    public InterviewResponse updateInterviewNotes(Long id, String notes, User recruiter) {

        Interview interview = interviewRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Interview not found with id: " + id));

        ensureRecruiterOwnsInterview(interview, recruiter);
        interview.setNotes(notes);

        Interview updatedInterview = interviewRepository.save(interview);

        return mapToResponse(updatedInterview);
    }


    private void ensureCanView(Application application, User user) {
        boolean candidate = application.getCandidate().getId().equals(user.getId());
        boolean recruiter = application.getJob().getRecruiter().getId().equals(user.getId());
        if (!candidate && !recruiter) {
            throw new com.omkar.hireflow.exception.ForbiddenException("You are not allowed to view this interview");
        }
    }

    private void ensureRecruiterOwnsInterview(Interview interview, User recruiter) {
        if (!interview.getApplication().getJob().getRecruiter().getId().equals(recruiter.getId())) {
            throw new com.omkar.hireflow.exception.ForbiddenException("You can only manage interviews for your own jobs");
        }
    }

    private InterviewResponse mapToResponse(Interview interview) {

        Application application = interview.getApplication();

        return new InterviewResponse(
                interview.getId(),
                application.getId(),
                application.getCandidate().getId(),
                application.getCandidate().getName(),
                application.getJob().getId(),
                application.getJob().getTitle(),
                interview.getScheduledAt(),
                interview.getMode(),
                interview.getMeetingLink(),
                interview.getLocation(),
                interview.getStatus(),
                interview.getNotes()
        );
    }
}