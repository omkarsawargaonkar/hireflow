package com.omkar.hireflow.service;

import com.omkar.hireflow.dto.ApplicationResponse;
import com.omkar.hireflow.entity.Application;
import com.omkar.hireflow.entity.ApplicationStatus;
import com.omkar.hireflow.entity.Job;
import com.omkar.hireflow.entity.User;
import com.omkar.hireflow.repository.ApplicationRepository;
import com.omkar.hireflow.repository.JobRepository;
import com.omkar.hireflow.repository.UserRepository;
import org.springframework.stereotype.Service;
import com.omkar.hireflow.entity.JobStatus;
import com.omkar.hireflow.exception.BusinessException;
import com.omkar.hireflow.exception.ResourceNotFoundException;

import java.util.List;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    public ApplicationService(ApplicationRepository applicationRepository,
                              UserRepository userRepository,
                              JobRepository jobRepository) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
    }

    public ApplicationResponse applyForJob(User candidate, Long jobId) {

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Job not found with id: " + jobId));

        if (job.getStatus() != JobStatus.OPEN) {
            throw new BusinessException(
                    "Applications are not accepted for this job");
        }

        if (applicationRepository.existsByCandidateAndJob(candidate, job)) {
            throw new BusinessException(
                    "Candidate has already applied for this job");
        }

        Application application = new Application(candidate, job);

        Application savedApplication =
                applicationRepository.save(application);

        return mapToResponse(savedApplication);
    }

    public List<ApplicationResponse> getApplicationsByCandidate(User candidate) {

        return applicationRepository.findByCandidate(candidate)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<ApplicationResponse> getApplicationsByJob(Long jobId, User recruiter) {

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Job not found with id: " + jobId));

        if (!job.getRecruiter().getId().equals(recruiter.getId())) {
            throw new com.omkar.hireflow.exception.ForbiddenException("You can only view applications for your own jobs");
        }

        return applicationRepository.findByJob(job)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public ApplicationResponse updateApplicationStatus(
            Long applicationId,
            ApplicationStatus newStatus,
            User recruiter) {

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Application not found with id: " + applicationId));

        if (!application.getJob().getRecruiter().getId().equals(recruiter.getId())) {
            throw new com.omkar.hireflow.exception.ForbiddenException("You can only update applications for your own jobs");
        }

        application.setStatus(newStatus);

        Application updatedApplication =
                applicationRepository.save(application);

        return mapToResponse(updatedApplication);
    }

    private ApplicationResponse mapToResponse(Application application) {

        return new ApplicationResponse(
                application.getId(),
                application.getCandidate().getId(),
                application.getCandidate().getName(),
                application.getCandidate().getEmail(),
                application.getJob().getId(),
                application.getJob().getTitle(),
                application.getJob().getCompanyName(),
                application.getStatus(),
                application.getAppliedAt(),
                application.getUpdatedAt()
        );
    }
}