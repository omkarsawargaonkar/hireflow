package com.omkar.hireflow.repository;

import com.omkar.hireflow.entity.Job;
import com.omkar.hireflow.entity.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import com.omkar.hireflow.entity.User;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByStatusOrderByCreatedAtDesc(JobStatus status);

    List<Job> findByRecruiterOrderByCreatedAtDesc(User recruiter);
}