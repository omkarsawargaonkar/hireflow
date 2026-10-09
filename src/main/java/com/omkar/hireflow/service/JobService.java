package com.omkar.hireflow.service;

import com.omkar.hireflow.exception.ResourceNotFoundException;
import com.omkar.hireflow.dto.JobRequest;
import com.omkar.hireflow.dto.JobResponse;
import com.omkar.hireflow.entity.Job;
import com.omkar.hireflow.entity.JobStatus;
import com.omkar.hireflow.entity.User;
import com.omkar.hireflow.repository.JobRepository;
import org.springframework.stereotype.Service;
import com.omkar.hireflow.repository.UserRepository;

import java.util.List;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public JobService(JobRepository jobRepository, UserRepository userRepository) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    public JobResponse createJob(JobRequest request, User recruiter) {

        Job job = new Job(
                request.getTitle(),
                request.getDescription(),
                request.getCompanyName(),
                request.getLocation(),
                request.getEmploymentType(),
                request.getSalary(),
                request.getSkills(),
                JobStatus.OPEN,
                recruiter
        );

        Job savedJob = jobRepository.save(job);

        return mapToResponse(savedJob);
    }

    public List<JobResponse> getOpenJobs() {

        return jobRepository
                .findByStatusOrderByCreatedAtDesc(JobStatus.OPEN)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public JobResponse getJobById(Long id) {

        Job job = jobRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Job not found with id: " + id));

        return mapToResponse(job);
    }

    public List<JobResponse> getJobsByRecruiter(User recruiter) {

        return jobRepository.findByRecruiterOrderByCreatedAtDesc(recruiter)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public JobResponse closeJob(Long jobId, User recruiter) {

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Job not found with id: " + jobId));

        if (!job.getRecruiter().getId().equals(recruiter.getId())) {
            throw new IllegalArgumentException(
                    "You are not authorized to close this job");
        }

        job.setStatus(JobStatus.CLOSED);

        Job updatedJob = jobRepository.save(job);

        return mapToResponse(updatedJob);
    }

    private JobResponse mapToResponse(Job job) {

        return new JobResponse(
                job.getId(),
                job.getTitle(),
                job.getDescription(),
                job.getCompanyName(),
                job.getLocation(),
                job.getEmploymentType(),
                job.getSalary(),
                job.getSkills(),
                job.getStatus(),
                job.getCreatedAt(),
                job.getRecruiter().getId()
        );
    }
}