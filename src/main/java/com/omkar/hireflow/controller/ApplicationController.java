package com.omkar.hireflow.controller;

import com.omkar.hireflow.dto.ApplicationResponse;
import com.omkar.hireflow.entity.ApplicationStatus;
import com.omkar.hireflow.entity.Role;
import com.omkar.hireflow.entity.User;
import com.omkar.hireflow.service.ApplicationService;
import com.omkar.hireflow.service.CurrentUserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final CurrentUserService currentUserService;

    public ApplicationController(ApplicationService applicationService, CurrentUserService currentUserService) {
        this.applicationService = applicationService;
        this.currentUserService = currentUserService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApplicationResponse applyForJob(@RequestParam Long jobId, HttpServletRequest httpRequest) {
        User candidate = currentUserService.requireRole(httpRequest, Role.CANDIDATE);
        return applicationService.applyForJob(candidate, jobId);
    }

    @GetMapping("/candidate/me")
    public List<ApplicationResponse> getMyApplications(HttpServletRequest httpRequest) {
        User candidate = currentUserService.requireRole(httpRequest, Role.CANDIDATE);
        return applicationService.getApplicationsByCandidate(candidate);
    }

    @GetMapping("/job/{jobId}")
    public List<ApplicationResponse> getApplicationsByJob(@PathVariable Long jobId,
                                                           HttpServletRequest httpRequest) {
        User recruiter = currentUserService.requireRole(httpRequest, Role.RECRUITER);
        return applicationService.getApplicationsByJob(jobId, recruiter);
    }

    @PatchMapping("/{id}/status")
    public ApplicationResponse updateApplicationStatus(@PathVariable Long id,
                                                        @RequestParam ApplicationStatus status,
                                                        HttpServletRequest httpRequest) {
        User recruiter = currentUserService.requireRole(httpRequest, Role.RECRUITER);
        return applicationService.updateApplicationStatus(id, status, recruiter);
    }
}
