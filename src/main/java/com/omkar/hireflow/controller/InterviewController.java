package com.omkar.hireflow.controller;

import com.omkar.hireflow.dto.InterviewRequest;
import com.omkar.hireflow.dto.InterviewResponse;
import com.omkar.hireflow.entity.Role;
import com.omkar.hireflow.entity.User;
import com.omkar.hireflow.entity.InterviewStatus;
import com.omkar.hireflow.service.CurrentUserService;
import com.omkar.hireflow.service.InterviewService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/interviews")
public class InterviewController {

    private final InterviewService interviewService;
    private final CurrentUserService currentUserService;

    public InterviewController(InterviewService interviewService, CurrentUserService currentUserService) {
        this.interviewService = interviewService;
        this.currentUserService = currentUserService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InterviewResponse scheduleInterview(@Valid @RequestBody InterviewRequest request,
                                               HttpServletRequest httpRequest) {
        User recruiter = currentUserService.requireRole(httpRequest, Role.RECRUITER);
        return interviewService.scheduleInterview(request, recruiter);
    }

    @GetMapping("/application/{applicationId}")
    public InterviewResponse getInterviewByApplicationId(@PathVariable Long applicationId,
                                                          HttpServletRequest httpRequest) {
        User user = currentUserService.requireUser(httpRequest);
        return interviewService.getInterviewByApplicationId(applicationId, user);
    }

    @GetMapping("/{id}")
    public InterviewResponse getInterviewById(@PathVariable Long id, HttpServletRequest httpRequest) {
        User user = currentUserService.requireUser(httpRequest);
        return interviewService.getInterviewById(id, user);
    }

    @PatchMapping("/{id}/status")
    public InterviewResponse updateInterviewStatus(@PathVariable Long id,
                                                   @RequestParam InterviewStatus status,
                                                   HttpServletRequest httpRequest) {
        User recruiter = currentUserService.requireRole(httpRequest, Role.RECRUITER);
        return interviewService.updateInterviewStatus(id, status, recruiter);
    }

    @PatchMapping("/{id}/notes")
    public InterviewResponse updateInterviewNotes(@PathVariable Long id,
                                                  @RequestParam String notes,
                                                  HttpServletRequest httpRequest) {
        User recruiter = currentUserService.requireRole(httpRequest, Role.RECRUITER);
        return interviewService.updateInterviewNotes(id, notes, recruiter);
    }
}
