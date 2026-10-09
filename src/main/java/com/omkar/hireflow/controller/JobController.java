package com.omkar.hireflow.controller;

import com.omkar.hireflow.dto.JobRequest;
import com.omkar.hireflow.dto.JobResponse;
import com.omkar.hireflow.entity.Role;
import com.omkar.hireflow.entity.User;
import com.omkar.hireflow.service.CurrentUserService;
import com.omkar.hireflow.service.JobService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;
    private final CurrentUserService currentUserService;

    public JobController(JobService jobService, CurrentUserService currentUserService) {
        this.jobService = jobService;
        this.currentUserService = currentUserService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public JobResponse createJob(@Valid @RequestBody JobRequest request,
                                 HttpServletRequest httpRequest) {
        User recruiter = currentUserService.requireRole(httpRequest, Role.RECRUITER);
        return jobService.createJob(request, recruiter);
    }

    @GetMapping
    public List<JobResponse> getOpenJobs() {
        return jobService.getOpenJobs();
    }

    @GetMapping("/recruiter/me")
    public List<JobResponse> getMyJobs(HttpServletRequest httpRequest) {
        User recruiter = currentUserService.requireRole(httpRequest, Role.RECRUITER);
        return jobService.getJobsByRecruiter(recruiter);
    }

    @GetMapping("/{id}")
    public JobResponse getJobById(@PathVariable Long id) {
        return jobService.getJobById(id);
    }

    @PatchMapping("/{id}/close")
    public JobResponse closeJob(@PathVariable Long id, HttpServletRequest httpRequest) {
        User recruiter = currentUserService.requireRole(httpRequest, Role.RECRUITER);
        return jobService.closeJob(id, recruiter);
    }
}
